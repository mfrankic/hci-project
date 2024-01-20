package com.example.client.trapezoids;

import com.example.client.vectors.Vector2d;

/**
 * This class is used to calculate a number of following trapezoids. Here with a Vec2 as a value
 */
public class Trapezoid2d {

    private Vector2d last = new Vector2d();

    /**
     * Calculates the current trapezoid
     * @param delta delta time to the last value
     * @param next current value
     * @return trapezoid
     */
    public Vector2d trapezoid(float delta, Vector2d next) {
        Vector2d result = last.mean(next).multiply(delta);
        last = next;
        return result;
    }

    /**
     * Resets the last value
     */
    public void reset() {
        last = new Vector2d();
    }

}
