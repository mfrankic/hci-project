package com.example.client.averages;

import com.example.client.vectors.Vector3d;

public class Average3d {

    private int index;
    private Vector3d[] elements;

    public Average3d(int length) {
        this.elements = new Vector3d[length];
        index = 0;
    }

    public Vector3d avg(Vector3d next) {

        elements[index % elements.length] = next;
        index++;


        Vector3d total = new Vector3d();
        int amount = Math.min(elements.length, index);
        for (int i = 0; i < amount; i++) {
            total.add(elements[i]);
        }

        return total.divide(amount);
    }

    public void reset() {
        elements = new Vector3d[elements.length];
        index = 0;
    }
}
