package com.example.client.averages;

import com.example.client.vectors.Vector3d;

/**
 * This class represents an average which operates in a window, remembering a given value of past samples to do the averaging with.
 * Here with a Vector3d as a value.
 */
public class Average3d {

    private int index;
    private Vector3d[] elements;

    /**
     * @param length length of the window in values
     */
    public Average3d(int length) {
        this.elements = new Vector3d[length];
        index = 0;
    }

    /**
     * Takes the next sample and calculates the current average
     * @param next next sample
     * @return current average, including next sample
     */
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

    /**
     * Resets the average
     */
    public void reset() {
        elements = new Vector3d[elements.length];
        index = 0;
    }
}
