package com.example.client.trapezoids;

import com.example.client.vectors.Vector2d;

public class Trapezoid2d {

    private Vector2d last = new Vector2d();

    public Vector2d trapezoid(float delta, Vector2d next) {
        Vector2d result = last.mean(next).multiply(delta);
        last = next;
        return result;
    }

    public void reset() {
        last = new Vector2d();
    }

}
