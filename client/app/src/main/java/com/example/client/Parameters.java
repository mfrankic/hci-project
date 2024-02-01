package com.example.client;

import android.content.SharedPreferences;

public class Parameters {


    private static final float DURATION_THRESHOLD = 0.1f;
    private static final float DURATION_WINDOW_GRAVITY = 0.02f;
    private static final float DURATION_WINDOW_NOISE = 0.01f;
    private static final float DURATION_GRAVITY = 2f;

    private static final float SENSITIVITY = 15000f;

    private static final boolean ENABLE_GRAVITY_ROTATION = false;

    private final SharedPreferences prefs;

    public Parameters(SharedPreferences preferences) {
        this.prefs = preferences;
    }

    public int getLengthWindowGravity() {
        return Math.round(prefs.getFloat("movementDurationWindowGravity", DURATION_WINDOW_GRAVITY)
                * prefs.getFloat("movementSampling", 500));
    }

    public int getLengthWindowNoise() {
        return Math.round(prefs.getFloat("movementDurationWindowNoise", DURATION_WINDOW_NOISE)
                * prefs.getFloat("movementSampling", 500));
    }

    public int getLengthThreshold() {
        return Math.round(prefs.getFloat("movementDurationThreshold", DURATION_THRESHOLD)
                * prefs.getFloat("movementSampling", 500));
    }

    public int getLengthGravity() {
        return Math.round(prefs.getFloat("movementDurationGravity", DURATION_GRAVITY)
                * prefs.getFloat("movementSampling", 500));
    }

    public float getSensitivity() {
        return prefs.getFloat("movementSensitivity", SENSITIVITY);
    }

    public float getThresholdAcceleration() {
        return prefs.getFloat("movementThresholdAcceleration", 0.03f);
    }

    public float getThresholdRotation() {
        return prefs.getFloat("movementThresholdRotation", 0.01f);
    }

    public boolean getEnableGravityRotation() {
        return prefs.getBoolean("movementEnableGravityRotation", ENABLE_GRAVITY_ROTATION);
    }

}
