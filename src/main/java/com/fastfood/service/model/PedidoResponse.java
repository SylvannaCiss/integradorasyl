package com.fastfood.service.model;

import lombok.Data;

@Data
public class PedidoResponse {

    private int id;
    private String nombreCliente;
    private String descripcion;
    private double monto; 
    private String estado;

    public PedidoResponse(Pedido pedido) { //Constructor para mapear desde la entidad pedido
        this.id = pedido.getId();
        this.nombreCliente = pedido.getNombreCliente();
        this.descripcion = pedido.getDescripcion();
        this.monto = pedido.getMonto();
        this.estado = pedido.getEstado();
    }

    //Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
