package com.fastfood.service.model;

public class PedidoRequest {
    
    private String nombreCliente;
    private String descripcion;
    private double monto; 
    

    public PedidoRequest(){}

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDescripcion() {  return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

}
