package com.example.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private NetworkThread networkThread;
    private Handler networkHandler;

    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object

    EditText ip;

    Boolean flag;
    Button start,lkm,rkm;
    int Px,Py,PxStart=0,PyStart=0,lk=0,rk=0,dx=0,dy=0,xstart=0,ystart=0;
    PrintWriter out;
    Socket s;
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        start = (Button)findViewById(R.id.start);

        lkm = (Button)findViewById(R.id.lkm);
        rkm = (Button)findViewById(R.id.rkm);

        ip = (EditText)findViewById(R.id.ip);
        //ip.setText();



        lkm.setOnClickListener(this);
        rkm.setOnClickListener(this);
        start.setOnClickListener(this);


        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

//        networkThread = new NetworkThread();
//        networkThread.start();
        HandlerThread networkThread = new HandlerThread("NetworkThread");
        networkThread.start();
        networkHandler = new Handler(networkThread.getLooper());

        networkHandler.post(() -> {
            try {
                s = new Socket("192.168.1.102", 5382);
                out = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){
        System.out.println("Sensor changed");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER || event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            Px=(int)x-PxStart;

            Py=(int)y-PyStart;
            //if (Py<0) Py=Math.abs(Py); else Py=360-Py;
            //Py=360-Py;
            Py=Py*(-1);

            xCoor.setText("X: "+Px);
            yCoor.setText("Y: "+Py);
            zCoor.setText("Z: "+z);

            // Send sensor data to server
            String message = event.sensor.getType() + ":" + x + "," + y + "," + z;

            networkHandler.post(() -> {
                if (out != null && !out.checkError()) {
                    out.println(message);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        try
        {

            if(viewId == R.id.start) {
                if (out == null) {
                    start.setText("Stop");
                    PxStart = Px;
                    PyStart = -Py;
                    s = new Socket(ip.getText().toString(), 5382);
                    out = new PrintWriter(s.getOutputStream(), true);
                    Toast.makeText(this, "Service is started.", Toast.LENGTH_LONG).show();
                } else {
                    start.setText("Start");
                    PxStart = 0;
                    PyStart = 0;
                    Toast.makeText(this, "Service is stopped.", Toast.LENGTH_LONG).show();
                    out = null;
                    s.shutdownOutput();
                    s.close();
                }
            } else if(viewId == R.id.lkm) {
                lk=1;
            } else if(viewId == R.id.rkm) {
                rk=1;
            } else {
                lk=0;
                rk=0;
            }
        }
        catch (Exception e) {}

    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                dx=0;
                dy=0;
                xstart=x;
                ystart=y;
                //Toast.makeText(this,"Down", Toast.LENGTH_LONG).show();
                //lrk.setText("Down"+x+" "+" "+y);
                break;
            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this,"Move", Toast.LENGTH_LONG).show();
                dx=(int)(xstart-x);
                dy=(int)(1000*(ystart-y)/800);

                break;
            case MotionEvent.ACTION_UP:
                dx=0;xstart=x;
                dy=0;ystart=y;
                //Toast.makeText(this,"Up", Toast.LENGTH_LONG).show();
                //lrk.setText("Up"+x+" "+" "+y);
                break;
        }

        return true;
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