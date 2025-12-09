package com.fastfood.service.model;

public class Pedido {
    private int id; 
    private String nombreCliente;
    private String descripcion;
    private double monto;
    private String estado; /* REGISTRADO, EN_PREPARACION, DESPACHADO, CANCELADO */


    public Pedido(){}

    public Pedido(int id, String nombreCliente, String descripcion, double monto, String estado) { //pedidos registrados
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
    }

    public Pedido copiaPedido() {
        return new Pedido(id, nombreCliente, descripcion, monto, estado);

    }

    //Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


}
