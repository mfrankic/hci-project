package com.example.client;

public class Threshold {

    private final int dropoff;
    private final float firstThreshold, secondThreshold;

    private int lastActive;

    public Threshold(int dropoff, float firstThreshold, float secondThreshold) {
        this.dropoff = dropoff;
        this.firstThreshold = firstThreshold;
        this.secondThreshold = secondThreshold;

        this.lastActive = dropoff;
    }

    public boolean active(float first, float second) {
        if (first > firstThreshold || second > secondThreshold) lastActive = 0;
        else lastActive++;

        return lastActive <= dropoff;
    }


}
