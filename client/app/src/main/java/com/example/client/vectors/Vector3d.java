package com.example.client.vectors;

public class Vector3d {
    public float x, y, z;

    public Vector3d() {}
    public Vector3d(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector2d xy() {
        return new Vector2d(x, y);
    }

    public Vector2d yz() {
        return new Vector2d(y, z);
    }

    public Vector2d xz() {
        return new Vector2d(x, z);
    }

    public float abs() {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3d copy() {
        return new Vector3d(x, y, z);
    }

    public Vector3d add(Vector3d other) {
        x += other.x;
        y += other.y;
        z += other.z;

        return this;
    }

    public Vector3d subtract(Vector3d other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;

        return this;
    }

    public Vector3d multiply(float factor) {
        x *= factor;
        y *= factor;
        z *= factor;

        return this;
    }

    public Vector3d divide(float divisor) {
        x /= divisor;
        y /= divisor;
        z /= divisor;

        return this;
    }

    public Vector3d negative() {
        return this.multiply(-1f);
    }

    public Vector3d mean(Vector3d second) {
        return this.copy().add(second).divide(2);
    }

    public Vector3d rotate(Vector3d rotation) {

        // Calculate sines and cosines that are used (for optimization)
        float sa = (float) Math.sin(rotation.x);
        float ca = (float) Math.cos(rotation.x);
        float sb = (float) Math.sin(rotation.y);
        float cb = (float) Math.cos(rotation.y);
        float sc = (float) Math.sin(rotation.z);
        float cc = (float) Math.cos(rotation.z);

        // Apply the rotation (matrix used: xyz)
        float newX = cb * cc * x + cb * (-sc) * y + sb * z;
        float newY = ((-sa) * (-sb) * cb + ca * sc) * x + ((-sa) * (-sb) * (-sc) + ca * cc) * y + (-sa) * cb * z;
        float newZ = (ca * (-sb) * cc + sa * sc) * x + (ca * (-sb) * (-sc) + sa * cc) * y + ca * cb * z;

        this.x = newX;
        this.y = newY;
        this.z = newZ;

        return this;
    }
}
