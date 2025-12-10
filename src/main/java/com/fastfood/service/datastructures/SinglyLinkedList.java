package com.fastfood.service.datastructures;
import java.util.Arrays;

import com.fastfood.service.model.Pedido;

public class SinglyLinkedList {

     private static class Node {
        Pedido data;
        Node next; 

        Node(Pedido data){
            this.data = data; 
        }

    }

    private Node head; 
    private int size; 

    public void add(Pedido data){ 
    Node nuevo = new Node(data);
    if(head == null){
        head = nuevo;
    } else {
        Node temporal = head; 
        while(temporal.next != null){
            temporal = temporal.next;
        }
        temporal.next = nuevo; 
    }
    size++;
}


    public Pedido findById(int id){ //Buscar por Id
        Node nuevo = head;
        while (nuevo != null) {
            if (nuevo.data.getId() == id) {
                return nuevo.data;
            }
            nuevo = nuevo.next;
        }
        return null;
    }

    public boolean removeById(int id) { //Eliminar por Id
        if (head == null) {
            return false;
        }

        if (head.data.getId() == id) {
            head = head.next;
            size--;
            return true;
        }

        Node nuevo = head;
        while (nuevo.next != null) {
            if (nuevo.next.data.getId() == id) {
                nuevo.next = nuevo.next.next; // Salta el nodo a eliminar
                size--;
                return true;
            }
            nuevo = nuevo.next;
        }
        return false; // Pedido no encontrado
    }

    public int size(){ //Tamaño de la lista 
        return size;
    }

    //RECURSIVIDAD
    public double calcularTotalRecursivo(){
        return calcularRecursivoInterno(head);
    }

    private double calcularRecursivoInterno(Node nodo){
        if(nodo == null) return 0; 
        return nodo.data.getMonto() + calcularRecursivoInterno(nodo.next);
    }

    //Convertir a Lista Java para devolver JSON en el controller 
    public Pedido[] toArray() {
    Pedido[] arreglo = new Pedido[size];
    Node current = head;
    int index = 0;

    while (current != null) {
        arreglo[index++] = current.data;
        current = current.next;
    }

    return arreglo;
    }

    public java.util.List<Pedido> getAll() {
    return new java.util.ArrayList<>(Arrays.asList(toArray()));
    }

    
}
