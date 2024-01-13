package com.example.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener {
    public static final String SERVER_IP = "192.168.1.102";
    public static final int SERVER_PORT = 5382;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private NetworkThread networkThread;
    private Handler networkHandler;
    private DatagramSocket udpSocket = null;
    private InetAddress serverAddr = null;

    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object

    EditText ip;

    Boolean flag;
    Button start,lkm,reset;
    int Px,Py,PxStart=0,PyStart=0,lk=0,rk=0,dx=0,dy=0,xstart=0,ystart=0;
    PrintWriter out;
    Socket s;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoor = findViewById(R.id.xcoor);
        yCoor = findViewById(R.id.ycoor);
        zCoor = findViewById(R.id.zcoor);
        start = findViewById(R.id.start);

        lkm = findViewById(R.id.lkm);
        reset = findViewById(R.id.reset);

        ip = findViewById(R.id.ip);
        ip.setText(SERVER_IP);

        lkm.setOnClickListener(this);
        reset.setOnClickListener(this);
        start.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        HandlerThread networkThread = new HandlerThread("NetworkThread");
        networkThread.start();
        networkHandler = new Handler(networkThread.getLooper());
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        if (
                udpSocket != null &&
                !udpSocket.isClosed() &&
                (
                    event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ||
                    event.sensor.getType() == Sensor.TYPE_GYROSCOPE
                )
        ) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText("X: " + x);
            yCoor.setText("Y: " + y);
            zCoor.setText("Z: " + z);

            String message = event.sensor.getType() + " " + x + " " + y + " " + z + " " + lk;
            byte[] messageBytes = message.getBytes();

            networkHandler.post(() -> {
                try {
                    DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddr, SERVER_PORT);
                    udpSocket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        try {
            if(viewId == R.id.start) {
                if (udpSocket == null || udpSocket.isClosed()) {
                    start.setText("Stop");
                    PxStart = Px;
                    PyStart = -Py;
                    serverAddr = InetAddress.getByName(ip.getText().toString());
                    udpSocket = new DatagramSocket();
                    Toast.makeText(this, "Service is started.", Toast.LENGTH_LONG).show();
                } else {
                    start.setText("Start");
                    PxStart = 0;
                    PyStart = 0;
                    Toast.makeText(this, "Service is stopped.", Toast.LENGTH_LONG).show();
                    if (udpSocket != null) {
                        udpSocket.close();
                        udpSocket = null;
                    }
                }
            } else if(viewId == R.id.lkm) {
                lk=1;
            } else if(viewId == R.id.reset) {
                rk=1;
            } else {
                lk=0;
                rk=0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        try {
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}