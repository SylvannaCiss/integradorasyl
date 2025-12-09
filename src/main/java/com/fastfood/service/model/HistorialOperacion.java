package com.fastfood.service.model;

public class HistorialOperacion {
    
    private String tipoOperacion; // "CREAR", "CANCELAR", "DESPACHAR"
    private Pedido pedidoAntes; // Estado del pedido antes de la operación (copia)
    private Pedido pedidoDespues; // Estado del pedido después de la operación (copia)

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        
        // Lógica del constructor de copia usando IF-ELSE para pedidoAntes ---
        if (pedidoAntes != null) {
            // Si el objeto Pedido existe, creamos una COPIA profunda para aislarlo.
            this.pedidoAntes = new Pedido(pedidoAntes); 
        } else {
            // Si es null (como en la operación CREAR), lo mantenemos como null.
            this.pedidoAntes = null;
        }

        // --- Lógica del constructor de copia usando IF-ELSE para pedidoDespues ---
        if (pedidoDespues != null) {
            // Si el objeto Pedido existe, creamos una COPIA profunda.
            this.pedidoDespues = new Pedido(pedidoDespues);
        } else {
            // Si es null, lo mantenemos como null.
            this.pedidoDespues = null;
        }
    }

    // Getters
    public String getTipoOperacion() { return tipoOperacion; }
    public Pedido getPedidoAntes() { return pedidoAntes; }
    public Pedido getPedidoDespues() { return pedidoDespues; }
}
