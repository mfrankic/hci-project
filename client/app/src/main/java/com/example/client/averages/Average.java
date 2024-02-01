package com.example.client.averages;


public class Average {

    private int index;
    private float[] elements;

    public Average(int length) {
        this.elements = new float[length];
        index = 0;
    }

    public float avg(float next) {
        elements[index % elements.length] = next;
        index++;

        float total = 0f;
        int amount = Math.min(elements.length, index);
        for (int i = 0; i < amount; i++) {
            total += elements[i];
        }

        return total / amount;
    }

    public void reset() {
        elements = new float[elements.length];
        index = 0;
    }
}
