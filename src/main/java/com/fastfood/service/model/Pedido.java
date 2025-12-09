package com.fastfood.service.model;

public class Pedido {
    private int id; 
    private String nombreCliente;
    private String descripcion;
    private double monto;
    private String estado; /* REGISTRADO, EN_PREPARACION, DESPACHADO, CANCELADO */

    private static int nextId = 1;

    public Pedido(){ //constructor vacio

    }

    public Pedido(int id, String nombreCliente, String descripcion, double monto, String estado) { //pedidos registrados
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
    }

    public Pedido(String nombreCliente, String descripcion, double monto) {
        this.id = nextId++;
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = "REGISTRADO"; // Estado por defecto
    }

    

    public Pedido(Pedido otro) { //Constructor que clona
        this.id = otro.id;
        this.nombreCliente = otro.nombreCliente;
        this.descripcion = otro.descripcion;
        this.monto = otro.monto;
        this.estado = otro.estado;
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
