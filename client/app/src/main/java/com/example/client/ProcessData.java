package com.example.client;

import com.example.client.averages.Average;
import com.example.client.averages.Average3d;
import com.example.client.trapezoids.Trapezoid2d;
import com.example.client.trapezoids.Trapezoid3d;
import com.example.client.vectors.Vector2d;
import com.example.client.vectors.Vector3d;

public class ProcessData {

    private Trapezoid3d rotationDeltaTrapezoid;

    private Average activeGravityAverage;
    private Average activeNoiseAverage;
    private Threshold activeThreshold;
    private boolean lastActive;

    private Average3d gravityInactiveAverage;
    private Vector3d gravityCurrent;

    private Trapezoid2d distanceVelocityTrapezoid;
    private Vector2d distanceVelocity;
    private Trapezoid2d distanceDistanceTrapezoid;
    private float sensitivity;
    // Gravity Rotation is disabled by default because it currently does not work as expected
    private boolean enableGravityRotation;

    public ProcessData(Parameters parameters) {
        rotationDeltaTrapezoid = new Trapezoid3d();

        activeGravityAverage = new Average(parameters.getLengthWindowGravity());
        activeNoiseAverage = new Average(parameters.getLengthWindowNoise());
        activeThreshold = new Threshold(parameters.getLengthThreshold(), parameters.getThresholdAcceleration(), parameters.getThresholdRotation());

        gravityInactiveAverage = new Average3d(parameters.getLengthGravity());
        gravityCurrent = new Vector3d();

        distanceVelocityTrapezoid = new Trapezoid2d();
        distanceDistanceTrapezoid = new Trapezoid2d();
        distanceVelocity = new Vector2d();
        sensitivity = parameters.getSensitivity();

        enableGravityRotation = parameters.getEnableGravityRotation();
    }

    public Vector2d next(float time, float delta, Vector3d acceleration, Vector3d angularVelocity) {
        // Integrate rotation to distance, since that is used more often
        Vector3d rotationDelta = rotationDeltaTrapezoid.trapezoid(delta, angularVelocity);
        boolean active = active(acceleration, angularVelocity);
        Vector2d linearAcceleration = gravity(active, acceleration, rotationDelta);
        Vector2d distance = distance(delta, active, linearAcceleration, rotationDelta);
        lastActive = active;

        return distance;
    }

    public boolean active(Vector3d acceleration, Vector3d angularVelocity) {

        // Calculate the acceleration activation
        float acc = acceleration.xy().abs();

        // Remove gravity or rather lower frequencies
        float gravity = activeGravityAverage.avg(acc);
        acc -= gravity;
        acc = Math.abs(acc);

        // Remove noise
        acc = activeNoiseAverage.avg(acc);

        // Calculate the rotation activation
        float rot = Math.abs(angularVelocity.z);

        // Do the threshold
        return activeThreshold.active(acc, rot);
    }

    public Vector2d gravity(boolean active, Vector3d acceleration, Vector3d rotationDelta) {

        // Differentiate between the user being active or not
        if (active) {

            // Reset average for next phase
            if (!lastActive) {
                gravityInactiveAverage.reset();
            }

            // Rotate current gravity
            if (enableGravityRotation) gravityCurrent.rotate(rotationDelta.copy().negative());

        } else {
            // Just calculate the average of the samples
            gravityCurrent = gravityInactiveAverage.avg(acceleration);
        }

        // Subtract the gravity
        return acceleration.xy().subtract(gravityCurrent.xy());
    }

    public Vector2d distance(float delta, boolean active, Vector2d linearAcceleration, Vector3d rotationDelta) {

        // Only calculate if it is active for optimization
        if (active){

            // Counter-rotate the velocity
            distanceVelocity.rotate(-rotationDelta.z);

            // Integrate to distance
            distanceVelocity.add(distanceVelocityTrapezoid.trapezoid(delta, linearAcceleration));
            return distanceDistanceTrapezoid.trapezoid(delta, distanceVelocity).multiply(sensitivity);

        } else {

            // Reset stuff
            if (lastActive) {
                distanceVelocity = new Vector2d();

                // Clean the trapezoids because they contain a last value
                distanceVelocityTrapezoid.reset();
                distanceDistanceTrapezoid.reset();
            }

            return new Vector2d();
        }
    }

}
