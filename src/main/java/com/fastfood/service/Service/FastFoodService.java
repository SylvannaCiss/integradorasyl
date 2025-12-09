package com.fastfood.service.Service;


import com.fastfood.service.datastructures.SinglyLinkedList;
import com.fastfood.service.datastructures.Queue;
import com.fastfood.service.datastructures.Stack;
import com.fastfood.service.model.Pedido;
import com.fastfood.service.model.HistorialOperacion;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class FastFoodService {

    // Estructuras de datos propias (In-Memory)
    private final SinglyLinkedList pedidosList = new SinglyLinkedList();
    private final Queue pedidosQueue = new Queue();
    private final Stack historialStack = new Stack();

    // 4.1. Registrar un nuevo pedido
    public Pedido registrarPedido(String nombreCliente, String descripcion, double monto) {
        Pedido nuevoPedido = new Pedido( nombreCliente, descripcion, monto);

        // 1. Añadir a la lista principal
        pedidosList.add(nuevoPedido);

        // 2. Encolar para despachar
        pedidosQueue.enqueue(nuevoPedido);

        // 3. Registrar en historial (CREAR: Antes=null, Después=nuevoPedido)
        historialStack.push(new HistorialOperacion("CREAR", null, nuevoPedido));

        return nuevoPedido;
    }

    // 4.2. Listar todos los pedidos
    public List<Pedido> listarTodos() {
        return pedidosList.getAll();
    }

    // 4.3. Consultar un pedido por id
    public Pedido buscarPorId(int id) {
        return pedidosList.findById(id);
    }

    // 4.4. Cancelar un pedido por id
    public Pedido cancelarPedido(int id) {
        Pedido pedidoACancelar = pedidosList.findById(id);
        if (pedidoACancelar == null) return null;

        Pedido estadoAntes = new Pedido(pedidoACancelar); // Copia del estado previo

        // 1. Cambiar estado en la lista
        pedidoACancelar.setEstado("CANCELADO");

        // 2. Eliminar de la cola si estaba pendiente
        pedidosQueue.removeById(id);

        // 3. Registrar en historial (CANCELAR)
        historialStack.push(new HistorialOperacion("CANCELAR", estadoAntes, pedidoACancelar));

        return pedidoACancelar;
    }

    // 4.5. Despachar el siguiente pedido
    public Pedido despacharSiguiente() {
        if (pedidosQueue.isEmpty()) return null;

        // 1. Sacar de la cola (FIFO)
        Pedido pedidoADespachar = pedidosQueue.dequeue();
        
        // El pedido sacado tiene la ID, pero necesitamos la referencia en la lista para actualizar
        Pedido pedidoEnLista = pedidosList.findById(pedidoADespachar.getId());

        if (pedidoEnLista == null) return null;

        Pedido estadoAntes = new Pedido(pedidoEnLista);

        // 2. Actualizar estado en la lista
        pedidoEnLista.setEstado("DESPACHADO");

        // 3. Registrar en historial (DESPACHAR)
        historialStack.push(new HistorialOperacion("DESPACHAR", estadoAntes, pedidoEnLista));

        return pedidoEnLista;
    }

    // 4.6. Obtener estadísticas de pedidos
    public Map<String, Object> obtenerEstadisticas() {
        List<Pedido> pedidos = pedidosList.getAll();
        int totalPedidos = pedidos.size();
        double totalMonto = calcularMontoTotalRecursivo();
        int registrados = 0;
        int despachados = 0;
        int cancelados = 0;

        for (Pedido p : pedidos) {
            switch (p.getEstado()) {
                case "REGISTRADO": registrados++; break;
                case "DESPACHADO": despachados++; break;
                case "CANCELADO": cancelados++; break;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPedidos", totalPedidos);
        stats.put("totalMonto", totalMonto);
        stats.put("totalRegistrados", registrados);
        stats.put("totalDespachados", despachados);
        stats.put("totalCancelados", cancelados);

        return stats;
    }

    // 4.7. Cálculo recursivo del monto total
    public double calcularMontoTotalRecursivo() {
        // Obtenemos todos los pedidos para pasarlos al auxiliar recursivo
        List<Pedido> pedidos = pedidosList.getAll();
        return calcularMontoRecursivoAux(pedidos, 0);
    }

    // Método auxiliar privado y recursivo (recorre la lista usando un índice)
    private double calcularMontoRecursivoAux(List<Pedido> pedidos, int index) {
        // Caso Base: Hemos llegado al final de la lista
        if (index >= pedidos.size()) {
            return 0.0;
        }
        // Caso Recursivo: Suma el monto actual y llama para el resto de la lista
        return pedidos.get(index).getMonto() + calcularMontoRecursivoAux(pedidos, index + 1);
    }


    // 4.8. Rollback de la última operación
    public HistorialOperacion rollback() {
        if (historialStack.isEmpty()) return null;

        HistorialOperacion ultimaOp = historialStack.pop();
        Pedido pedidoAntes = ultimaOp.getPedidoAntes();
        Pedido pedidoDespues = ultimaOp.getPedidoDespues(); // Estado que vamos a revertir

        // Se busca el pedido real en la lista (excepto si es CREAR, que lo borramos)
        Pedido pedidoEnLista = pedidosList.findById(pedidoDespues.getId());

        switch (ultimaOp.getTipoOperacion()) {
            case "CREAR":
                // Acciones: Eliminar de la lista y la cola.
                pedidosList.removeById(pedidoDespues.getId());
                pedidosQueue.removeById(pedidoDespues.getId());
                break;

            case "CANCELAR":
            case "DESPACHAR":
                // Acciones: Restaurar el estado anterior.
                if (pedidoEnLista != null) {
                    pedidoEnLista.setEstado(pedidoAntes.getEstado());
                    // Si el estado restaurado es pendiente, vuelve a la cola.
                    if ("REGISTRADO".equals(pedidoAntes.getEstado()) || "EN_PREPARACION".equals(pedidoAntes.getEstado())) {
                        pedidosQueue.enqueue(pedidoEnLista);
                    }
                }
                break;
        }

        return ultimaOp;
    }
}