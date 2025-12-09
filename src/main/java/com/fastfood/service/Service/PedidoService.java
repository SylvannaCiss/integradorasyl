package com.fastfood.service.Service;

import com.fastfood.service.datastructures.Queue;
import com.fastfood.service.datastructures.Stack;
import com.fastfood.service.datastructures.SinglyLinkedList;
import com.fastfood.service.model.*;



import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final SinglyLinkedList pedidos = new SinglyLinkedList();
    private final Queue colaPendientes = new Queue(8); // Capacidad de ejemplo
    private final Stack historial = new Stack(16); // Capacidad de ejemplo
    private int nextId = 1;

    // VALIDACIÓN (Lanza IllegalArgumentException si falla)
    private void validar(PedidoRequest request) {
        if (request.getNombreCliente() == null || request.getNombreCliente().trim().isEmpty()){
            throw new IllegalArgumentException("nombreCliente es obligatorio");
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()){
            throw new IllegalArgumentException("descripcion es obligatoria");
        }

        if (request.getMonto() <= 0){
            throw new IllegalArgumentException("monto debe ser mayor a 0");
        }
    }

    public PedidoResponse crear(PedidoRequest request) {
        validar(request);

        Pedido p = new Pedido(
                nextId++,
                request.getNombreCliente().trim(),
                request.getDescripcion().trim(),
                request.getMonto(),
                "REGISTRADO"
        );

        pedidos.agregarPedido(p);
        colaPendientes.enqueue(p);
        historial.push(new HistorialOperacion("CREAR", null, p.copia()));

        return new PedidoResponse(p);
    }

    public PedidoResponse[] listar() {
        Pedido[] arr = pedidos.convertirListaAArreglo();
        PedidoResponse[] resp = new PedidoResponse[arr.length];

        for (int i = 0; i < arr.length; i++) {
            resp[i] = new PedidoResponse(arr[i]);
        }

        return resp;
    }

    public PedidoResponse consultar(int id) {
        Pedido p = pedidos.buscarPedidoPorId(id);
        if (p == null) return null;

        return new PedidoResponse(p);
    }

    public PedidoResponse cancelar(int id) {
        Pedido p = pedidos.buscarPedidoPorId(id);
        if (p == null) return null;

        Pedido antes = p.copia();
        p.setEstado("CANCELADO");

        colaPendientes.removeById(id);
        historial.push(new HistorialOperacion("CANCELAR", antes, p.copia()));

        return new PedidoResponse(p);
    }

    public PedidoResponse despacharSiguiente() {
        Pedido p = colaPendientes.dequeue();
        if (p == null) return null;

        Pedido antes = p.copia();
        p.setEstado("DESPACHADO");

        historial.push(new HistorialOperacion("DESPACHAR", antes, p.copia()));

        return new PedidoResponse(p);
    }

    public EstadisticasResponse estadisticas() {
        final int[] cont = new int[]{0, 0, 0}; // [REGISTRADO, DESPACHADO, CANCELADO]
        final double[] total = new double[]{0.0};

        pedidos.recorrerLista(new SinglyLinkedList.Visitor() {
            @Override
            public void visitarPedido(Pedido p) {
                total[0] += p.getMonto();
                String e = p.getEstado();

                if ("REGISTRADO".equals(e) || "EN_PREPARACION".equals(e)) cont[0]++;
                else if ("DESPACHADO".equals(e)) cont[1]++;
                else if ("CANCELADO".equals(e)) cont[2]++;
            }
        });

        return new EstadisticasResponse(
                pedidos.obtenerCantidadElementos(),
                total[0],
                cont[0],
                cont[1],
                cont[2]
        );
    }

    public double totalRecursivo() {
        return pedidos.calcularMontoTotalRecursivo();
    }

    public PedidoResponse rollback() {
        if (historial.isEmpty()) return null;

        HistorialOperacion op = historial.pop();
        String tipo = op.getTipoOperacion();

        if ("CREAR".equals(tipo)) {
            Pedido creado = op.getPedidoDespues();
            pedidos.eliminarPedidoPorId(creado.getId());
            colaPendientes.removeById(creado.getId());

            return new PedidoResponse(creado);
        }

        else if ("CANCELAR".equals(tipo) || "DESPACHAR".equals(tipo)) {
            Pedido antes = op.getPedidoAntes();
            Pedido actual = pedidos.buscarPedidoPorId(antes.getId());

            if (actual != null) {
                actual.setEstado(antes.getEstado());

                if ("REGISTRADO".equals(actual.getEstado()) ||
                    "EN_PREPARACION".equals(actual.getEstado())) {

                    colaPendientes.enqueue(actual);
                }
                return new PedidoResponse(actual);
            }
        }
        
        return null;
    }
}