package com.example.client.vectors;

public class Vector2d {
    public float x, y;

    public Vector2d() {}
    public Vector2d(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a copy of the vector
     */
    public Vector2d copy() {
        return new Vector2d(x, y);
    }

    /**
     * Returns the absolute value, aka. length of the vector
     */
    public float abs() {
        return (float) Math.sqrt(x*x + y*y);
    }

    /**
     * Adds another vector to this one
     */
    public Vector2d add(Vector2d other) {
        x += other.x;
        y += other.y;

        return this;
    }

    /**
     * Adds another vector to this one
     */
    public Vector2d subtract(Vector2d other) {
        x -= other.x;
        y -= other.y;

        return this;
    }

    /**
     * Multiplies or scales this vector by an amount
     */
    public Vector2d multiply(float factor) {
        x *= factor;
        y *= factor;

        return this;
    }

    /**
     * Divides this vector by a given amount
     */
    public Vector2d divide(float divisor) {
        x /= divisor;
        y /= divisor;

        return this;
    }

    /**
     * Calculates the average between this and a given other vector. Not affecting this instance.
     */
    public Vector2d mean(Vector2d second) {
        return this.copy().add(second).divide(2);
    }

    /**
     * Rotates this vector by a given rotation
     * @param rotation rotation in radians
     */
    public Vector2d rotate(float rotation) {
        float c = (float) Math.cos(rotation);
        float s = (float) Math.sin(rotation);

        float newX = c * x + (-s) * y;
        float newY = s * x + c * y;

        this.x = newX;
        this.y = newY;

        return this;
    }
}
