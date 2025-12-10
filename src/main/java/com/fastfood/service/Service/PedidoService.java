package com.fastfood.service.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fastfood.service.datastructures.Queue;
import com.fastfood.service.datastructures.Stack;
import com.fastfood.service.datastructures.SinglyLinkedList;
import com.fastfood.service.model.HistorialOperacion;
import com.fastfood.service.model.Pedido;

@Service
public class PedidoService {

    private SinglyLinkedList listaPedidos;
    private Queue colaPedidos;
    private Stack historial;

    private int ultimoId = 0;

    public PedidoService() {
        this.listaPedidos = new SinglyLinkedList();
        this.colaPedidos = new Queue();
        this.historial = new Stack();
    }

    public Pedido registrarPedido(Pedido request) {

        if (request.getNombreCliente() == null || request.getNombreCliente().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }

        if (request.getDescripcion() == null || request.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }

        if (request.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        Pedido nuevoPedido = new Pedido(
                ++ultimoId,
                request.getNombreCliente(),
                request.getDescripcion(),
                request.getMonto(),
                "REGISTRADO"
        );

        listaPedidos.add(nuevoPedido);
        colaPedidos.enqueue(nuevoPedido);

        historial.push(new HistorialOperacion(
                "CREAR",
                null,
                nuevoPedido
        ));

        return nuevoPedido;
    }

    public Pedido[] listarPedidos() {
        return listaPedidos.toArray();
    }

    
    public Pedido encontrarPedido(int id) {
        return listaPedidos.findById(id);
    }

    public Pedido cancelarPedido(int id) {

        Pedido pedido = listaPedidos.findById(id);

        if (pedido == null) {
            return null;
        }

        Pedido estadoAnterior = new Pedido(
                pedido.getId(),
                pedido.getNombreCliente(),
                pedido.getDescripcion(),
                pedido.getMonto(),
                pedido.getEstado()
        );

        pedido.setEstado("CANCELADO");
        colaPedidos.removeById(id);

        historial.push(new HistorialOperacion(
                "CANCELAR",
                estadoAnterior,
                pedido
        ));

        return pedido;
    }

    public Pedido despacharPedido() {

        if (colaPedidos.isEmpty()) {
            throw new IllegalStateException("No hay pedidos en cola.");
        }

        Pedido pedido = colaPedidos.dequeue();

        Pedido estadoAnterior = new Pedido(
                pedido.getId(),
                pedido.getNombreCliente(),
                pedido.getDescripcion(),
                pedido.getMonto(),
                pedido.getEstado()
        );

        listaPedidos.findById(pedido.getId()).setEstado("DESPACHADO");

        historial.push(new HistorialOperacion(
                "DESPACHAR",
                estadoAnterior,
                new Pedido(
                        pedido.getId(),
                        pedido.getNombreCliente(),
                        pedido.getDescripcion(),
                        pedido.getMonto(),
                        "DESPACHADO"
                )
        ));

        return pedido;
    }


    public Map<String, Object> mostrarEstadisticas() {

        Pedido[] pedidos = listaPedidos.toArray();

        int totalPedidos = pedidos.length;
        double totalMonto = listaPedidos.calcularTotalRecursivo();

        int totalRegistrados = 0;
        int totalDespachados = 0;
        int totalCancelados = 0;

        for (Pedido p : pedidos) {
            switch (p.getEstado()) {
                case "REGISTRADO":
                    totalRegistrados++;
                    break;
                case "DESPACHADO":
                    totalDespachados++;
                    break;
                case "CANCELADO":
                    totalCancelados++;
                    break;
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalPedidos", totalPedidos);
        resultado.put("totalMonto", totalMonto);
        resultado.put("totalRegistrados", totalRegistrados);
        resultado.put("totalDespachados", totalDespachados);
        resultado.put("totalCancelados", totalCancelados);

        return resultado;
    }


    public String calcularTotal() {
        double total = listaPedidos.calcularTotalRecursivo();
        return "El total es de: " + total;
    }

    public HistorialOperacion rollback() {

        if (historial.isEmpty()) {
            return null;
        }

        HistorialOperacion op = historial.pop();

        Pedido antes = op.getPedidoAntes();
        Pedido despues = op.getPedidoDespues();

        switch (op.getTipoOperacion()) {

            case "CREAR":
                if (despues != null) {
                    listaPedidos.removeById(despues.getId());
                    colaPedidos.removeById(despues.getId());
                }
                break;

            case "CANCELAR":
                if (antes != null) {
                    Pedido p = listaPedidos.findById(antes.getId());
                    if (p != null) {
                        p.setEstado(antes.getEstado());
                    }
                    if (antes.getEstado().equals("REGISTRADO")) {
                        colaPedidos.enqueue(p);
                    }
                }
                break;

            case "DESPACHAR":
                if (antes != null) {
                    Pedido p = listaPedidos.findById(antes.getId());
                    if (p != null) {
                        p.setEstado(antes.getEstado());
                        colaPedidos.enqueue(p);
                    }
                }
                break;
        }

        return op;
    }
}
