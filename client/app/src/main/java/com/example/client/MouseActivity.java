package com.example.client;

import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MouseActivity extends AppCompatActivity implements View.OnClickListener {

    private MouseInputs mouse;
    private MovementHandler movement;
    private boolean instanceSaved = false;

    String ipAddress;
    int port;
    Button start, lkm, reset, settings;
    TextView textIpAddress, textPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        Intent intent = getIntent();

        start = findViewById(R.id.start);

        lkm = findViewById(R.id.lkm);
        reset = findViewById(R.id.reset);
        settings = findViewById(R.id.buttonSettings);

        ipAddress = intent.getStringExtra("IP_ADDRESS");
        if (ipAddress == null) ipAddress = DefaultValues.IpAddress;

        port = intent.getIntExtra("PORT", DefaultValues.Port);

        textIpAddress = findViewById(R.id.textViewIpAddress);
        textPort = findViewById(R.id.textViewPort);

        textIpAddress.setText(ipAddress);
        textPort.setText(Integer.toString(port));

        lkm.setOnClickListener(this);
        reset.setOnClickListener(this);
        start.setOnClickListener(this);
        settings.setOnClickListener(this);

        this.mouse = new MouseInputs(this);
        this.movement = new MovementHandler(this, mouse);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWindow().getInsetsController().hide(WindowInsets.Type.mandatorySystemGestures());
            this.getWindow().getInsetsController().hide(WindowInsets.Type.systemGestures());
            this.getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars());
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

    @Override
    public void onDestroy() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWindow().getInsetsController().show(WindowInsets.Type.mandatorySystemGestures());
            this.getWindow().getInsetsController().show(WindowInsets.Type.systemGestures());
            this.getWindow().getInsetsController().show(WindowInsets.Type.navigationBars());
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        try {
            if(viewId == R.id.start) {
                if (movement.udpSocket == null || movement.udpSocket.isClosed()) {
                    start.setText("Stop");
                    movement.serverAddr = InetAddress.getByName(ipAddress);
                    movement.serverPort = port;
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
            } else if(viewId == R.id.buttonSettings) {
                start.setText("Start");
                Toast.makeText(this, "Service is stopped.", Toast.LENGTH_LONG).show();
                if (movement.udpSocket != null) {
                    movement.udpSocket.close();
                    movement.udpSocket = null;
                }

                this.finish();
            } else if(viewId == R.id.reset) {
                movement.inputs.setResetButton(viewId == R.id.reset);
            } else {
                movement.inputs.setLeftButton(viewId == R.id.lkm);
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
