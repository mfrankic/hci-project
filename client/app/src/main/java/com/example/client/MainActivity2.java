package com.example.client;

import static java.security.AccessController.getContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MainActivity2 extends Activity implements View.OnClickListener {

    private static final String SERVER_IP = "192.168.1.102";
    private static final int SERVER_PORT = 5382;
    private RelativeLayout root;
    private int width, height;
    private boolean theme; // false = light, true = dark

    private MouseInputs mouse;
    private MovementHandler movement;

    // Feedback
    private boolean visuals;
    private int buttonsStrokeWeight;
    private float viewIntensity;

    private boolean vibrations;
    private int buttonIntensity;
    private int buttonLength;
    private int scrollIntensity;
    private int scrollLength;
    private int specialIntensity;
    private int specialLength;

    // Buttons
    private float buttonsHeight;
    private float buttonsMiddleWidth;

    private int leftX, leftY, leftWidth, leftHeight;

    boolean left;
    private boolean instanceSaved = false;

    EditText ip;
    Button start, lkm, reset;
    int Px,Py,PxStart=0,PyStart=0,lk=0,rk=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);

        lkm = findViewById(R.id.lkm);
        reset = findViewById(R.id.reset);

        ip = findViewById(R.id.ip);
        ip.setText(SERVER_IP);

        lkm.setOnClickListener(this);
        reset.setOnClickListener(this);
        start.setOnClickListener(this);

        this.mouse = new MouseInputs(this);
        this.movement = new MovementHandler(this, mouse);

        readSettings(); // Read settings first to determine whether visuals are turned on

        // Set system view visibility
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!visuals)
                this.getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());

            this.getWindow().getInsetsController().hide(WindowInsets.Type.mandatorySystemGestures());
            this.getWindow().getInsetsController().hide(WindowInsets.Type.systemGestures());
            this.getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars());
        } else {
            if (!visuals)
                this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        movement.create();
        movement.register();
    }

    @Override
    protected void onStart() {
        super.onStart();
        instanceSaved = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        instanceSaved = true;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        instanceSaved = false;
    }

    /**
     * Reads the settings for the fragment from the preferences.
     */
    private void readSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        theme = prefs.getString("interfaceTheme", "dark").equals("dark");

        visuals = prefs.getBoolean("interfaceVisualsEnable", true);
        buttonsStrokeWeight = prefs.getInt("interfaceVisualsStrokeWeight", 4);
        viewIntensity = prefs.getFloat("interfaceVisualsIntensity", 0.5f);

        vibrations = prefs.getBoolean("interfaceVibrationsEnable", true);
        buttonIntensity = prefs.getInt("interfaceVibrationsButtonIntensity", 100);
        buttonLength = prefs.getInt("interfaceVibrationsButtonLength", 30);
        scrollIntensity = prefs.getInt("interfaceVibrationsScrollIntensity", 50);
        scrollLength = prefs.getInt("interfaceVibrationsScrollLength", 20);
        specialIntensity = prefs.getInt("interfaceVibrationsSpecialIntensity", 100);
        specialLength = prefs.getInt("interfaceVibrationsSpecialLength", 50);

        buttonsHeight = prefs.getFloat("interfaceLayoutHeight", 0.3f);
        buttonsMiddleWidth = prefs.getFloat("interfaceLayoutMiddleWidth", 0.2f);
    }

    @Override
    public void onDestroy() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!visuals)
                this.getWindow().getInsetsController().show(WindowInsets.Type.statusBars());

            this.getWindow().getInsetsController().show(WindowInsets.Type.mandatorySystemGestures());
            this.getWindow().getInsetsController().show(WindowInsets.Type.systemGestures());
            this.getWindow().getInsetsController().show(WindowInsets.Type.navigationBars());
        } else {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        super.onDestroy();
    }

    private boolean viewTouched(MotionEvent event) {
        // Temporary Variables
        boolean left = false;

        // Check whether a pointer is on a button, and if, check whether it is currently releasing or not
        for (int i = 0; i < event.getPointerCount(); i++) {
            if (within(event.getX(i), event.getY(i), leftX, leftY, leftWidth, leftHeight)) { // Left Mouse Button
                if ((event.getActionIndex() == i && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP && event.getActionMasked() != MotionEvent.ACTION_UP) || event.getActionIndex() != i)
                    left = true;
            }
        }

        if (this.left != left) mouse.setLeftButton(left);

        // Update self
        this.left = left;

        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        try {
            if(viewId == R.id.start) {
                if (movement.udpSocket == null || movement.udpSocket.isClosed()) {
                    start.setText("Stop");
                    movement.serverAddr = InetAddress.getByName(ip.getText().toString());
                    movement.udpSocket = new DatagramSocket();
                    Toast.makeText(this, "Service is started.", Toast.LENGTH_LONG).show();
                } else {
                    start.setText("Start");
                    Toast.makeText(this, "Service is stopped.", Toast.LENGTH_LONG).show();
                    if (movement.udpSocket != null) {
                        movement.udpSocket.close();
                        movement.udpSocket = null;
                    }
                }
            } else if(viewId == R.id.reset) {
                movement.inputs.setResetButton(viewId == R.id.reset);
            } else {
                movement.inputs.setLeftButton(viewId == R.id.lkm);
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether certain coordinates are within a boundary.
     *
     * @param touchX x coordinate
     * @param touchY y coordinate
     * @param x      x coordinate of the boundary
     * @param y      y coordinate of the boundary
     * @param width  width of the boundary
     * @param height height of the boundary
     * @return whether it is inside
     */
    private static boolean within(float touchX, float touchY, int x, int y, int width, int height) {
        return touchX > x && touchX < x + width && touchY > y && touchY < y + height;
    }
}
