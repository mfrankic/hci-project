package com.example.client;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.client.vectors.Vector2d;
import com.example.client.vectors.Vector3d;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MovementHandler implements SensorEventListener {

    private static final float NANO_FULL_FACTOR = 1e-9f;

    public static final int SENSOR_TYPE_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    public static final int SENSOR_TYPE_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    public static final int SAMPLING_RATE = SensorManager.SENSOR_DELAY_FASTEST;

    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private boolean registered;


    private Vector3d gyroSample = new Vector3d(); // TODO: Make this a buffer to accommodate for vastly different sampling rates
    private long lastTime = 0;
    private long firstTime = 0;
    private ProcessData processData;

    private final Context context;
    final MouseInputs inputs;
    private Handler networkHandler;
    DatagramSocket udpSocket = null;
    InetAddress serverAddr = null;
    int serverPort;

    public MovementHandler(Context context, MouseInputs inputs) {
        this.context = context;
        this.inputs = inputs;

        HandlerThread networkThread = new HandlerThread("NetworkThread");
        networkThread.start();
        networkHandler = new Handler(networkThread.getLooper());

        fetchSensor();
    }

    public void create() {
        processData = new ProcessData(new Parameters(PreferenceManager.getDefaultSharedPreferences(context)));
    }

    private void fetchSensor() {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(SENSOR_TYPE_ACCELEROMETER);
        gyroscope = manager.getDefaultSensor(SENSOR_TYPE_GYROSCOPE);
    }

    public void register() {
        if (registered) return;
        manager.registerListener(this, accelerometer, SAMPLING_RATE);
        manager.registerListener(this, gyroscope, SAMPLING_RATE);

        lastTime = 0;
        firstTime = 0;
        registered = true;
    }

    public void unregister() {
        if (!registered) return;
        manager.unregisterListener(this, accelerometer);
        manager.unregisterListener(this, gyroscope);

        registered = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!registered) return;

        if (udpSocket != null && !udpSocket.isClosed()) {
            String message = "";
            if (event.sensor.getType() == SENSOR_TYPE_ACCELEROMETER) {
                if (firstTime == 0) {
                    lastTime = event.timestamp;
                    firstTime = event.timestamp;
                    return;
                }

                float delta = (event.timestamp - lastTime) * NANO_FULL_FACTOR;
                float time = (event.timestamp - firstTime) * NANO_FULL_FACTOR;

                Log.v("VALUES", "Values: " + event.values[0] + " " + event.values[1] + " " + event.values[2]);

                Vector3d acceleration = new Vector3d(event.values[0], event.values[1], event.values[2]);

                Vector2d distance = processData.next(time, delta, acceleration, gyroSample);
                inputs.changeXPosition(distance.x);
                inputs.changeYPosition(-distance.y);

                message = distance.x + " "
                        + (-distance.y) + " "
                        + Boolean.compare(inputs.getLeftButton(), false) + " "
                        + Boolean.compare(inputs.getResetButton(), false);

                lastTime = event.timestamp;

                if (inputs.getLeftButton()) {
                    inputs.setLeftButton(false);
                };

                if (inputs.getResetButton()) {
                    this.unregister();
                    inputs.setResetButton(false);
                };
            } else if (event.sensor.getType() == SENSOR_TYPE_GYROSCOPE) {
                this.gyroSample = new Vector3d(event.values[0], event.values[1], event.values[2]);
            }

            if (message.equals("")) return;

            byte[] messageBytes = message.getBytes();

            networkHandler.post(() -> {
                try {
                    DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddr, serverPort);
                    udpSocket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            this.register();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void reset() {
        firstTime = 0;
        lastTime = 0;


    }
}
