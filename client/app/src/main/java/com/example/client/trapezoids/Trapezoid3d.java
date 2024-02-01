package com.example.client.trapezoids;

import com.example.client.vectors.Vector3d;

public class Trapezoid3d {

    private Vector3d last = new Vector3d();

    public Vector3d trapezoid(float delta, Vector3d next) {
        Vector3d result = last.mean(next).multiply(delta);
        last = next;
        return result;
    }

    public void reset() {
        last = new Vector3d();
    }

}
