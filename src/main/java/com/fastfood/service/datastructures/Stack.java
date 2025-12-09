package com.fastfood.service.datastructures;

import com.fastfood.service.model.HistorialOperacion;


class StackNode {
    
    HistorialOperacion data;
    StackNode next;

    public StackNode(HistorialOperacion data) {
        this.data = data;
        this.next = null;
    }
}

public class Stack {
    private StackNode top;

    // Operación: Push (Agregar al tope)
    public void push(HistorialOperacion op) {
        StackNode newNode = new StackNode(op);
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

    // Operación: isEmpty
    public boolean isEmpty() {
        return top == null;
    }
}
