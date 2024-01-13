package com.example.client.trapezoids;

import com.example.client.vectors.Vector3d;

/**
 * This class is used to calculate a number of following trapezoids. Here with a Vec3 as a value
 */
public class Trapezoid3d {

    private Vector3d last = new Vector3d();

    /**
     * Calculates the current trapezoid
     * @param delta delta time to the last value
     * @param next current value
     * @return trapezoid
     */
    public Vector3d trapezoid(float delta, Vector3d next) {
        Vector3d result = last.mean(next).multiply(delta);
        last = next;
        return result;
    }

    /**
     * Resets the last value
     */
    public void reset() {
        last = new Vector3d();
    }

}
