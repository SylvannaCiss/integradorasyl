package com.fastfood.service.model;

public class HistorialOperacion {
    
    private String tipoOperacion; // "CREAR", "CANCELAR", "DESPACHAR"
    private Pedido pedidoAntes; // Estado del pedido antes de la operación (copia)
    private Pedido pedidoDespues; // Estado del pedido después de la operación (copia)

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        
        if (pedidoAntes != null) {
            this.pedidoAntes = pedidoAntes.copiaPedido(); 
        } else {
            this.pedidoAntes = null;
        }
        if (pedidoDespues != null) {
            
            this.pedidoDespues = pedidoDespues.copiaPedido();
        } else {
            this.pedidoDespues = null;
        }
    }

    // Getters
    public String getTipoOperacion() { return tipoOperacion; }
    public Pedido getPedidoAntes() { return pedidoAntes; }
    public Pedido getPedidoDespues() { return pedidoDespues; }
}
