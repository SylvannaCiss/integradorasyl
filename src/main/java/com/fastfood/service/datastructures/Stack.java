package com.fastfood.service.datastructures;

import com.fastfood.service.model.HistorialOperacion;


public class Stack {
    
    private HistorialOperacion[] data; 
    private int top;

    public Stack(int capacity) {
        data = new HistorialOperacion[capacity];
        top = -1;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    pub



    
    public void push(HistorialOperacion op) {
        Node newNode = new Node(op);
        newNode.next = top;
        top = newNode;
    }

    // Operación: Pop (Sacar del tope)
    public HistorialOperacion pop() {
        if (isEmpty()) {
            return null;
        }
        HistorialOperacion op = top.data;
        top = top.next;
        return op;
    }

    
   
}
