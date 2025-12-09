package com.fastfood.service.datastructures;
import java.util.ArrayList;
import java.util.List;

import com.fastfood.service.model.Pedido;

public class Queue {
    private Node front;
    private Node rear;

    private static class Node {
        Pedido data; 
        Node next; 
        Node(Pedido data){
            this.data = data;
        }
    }

    // Operación: Enqueue (Agregar al final)
    public void enqueue(Pedido pedido) {
        Node nuevoNodo = new Node(pedido);
        if (rear == null) {
            front = nuevoNodo;
            rear = nuevoNodo;
        } else {
            rear.next = nuevoNodo;
            rear = nuevoNodo;
        }
    }

    // Operación: Dequeue (Sacar del frente)
    public Pedido dequeue() {
        if (isEmpty()) {
            return null;
        }
        Pedido pedido = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        return pedido;
    }

    // Operación: RemoveById (Necesario para CANCELAR y Rollback)
    public boolean removeById(int id) {
        if (front == null) return false;

        if (front.data.getId() == id) {
            front = front.next;
            if (front == null) rear = null;
            return true;
        }

        Node current = front;
        while (current.next != null) {
            if (current.next.data.getId() == id) {
                current.next = current.next.next;
                if (current.next == null) rear = current; 
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Operación: isEmpty
    public boolean isEmpty() {
        return front == null;
    }

    public List<Pedido> getAll(){
        List<Pedido> lista = new ArrayList<>();
        Node aux = front;
        while(aux != null){
            lista.add(aux.data);
            aux = aux.next;
        }
        return lista; 
        
    }
}