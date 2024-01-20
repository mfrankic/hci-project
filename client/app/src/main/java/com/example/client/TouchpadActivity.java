package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TouchpadActivity extends AppCompatActivity {

    String ipAddress;
    int port;

    Button buttonOnOff;
    Button buttonSettings;
    Button buttonLeftClick;
    View viewTouchpad;

    TextView textIpAddress;
    TextView textPort;

    int previousX = 0, previousY = 0;

    long startTime;
    long clickTimeMillis = 100;

    boolean isSendingData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpad);

        Intent intent = getIntent();

        ipAddress = intent.getStringExtra("IP_ADDRESS");
        if (ipAddress == null) ipAddress = DefaultValues.IpAddress;

        port = intent.getIntExtra("PORT", DefaultValues.Port);

        textIpAddress = findViewById(R.id.textViewIpAddress);
        textPort = findViewById(R.id.textViewPort);

        buttonOnOff = findViewById(R.id.buttonTurnOnOff);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLeftClick = findViewById(R.id.buttonLeftClick);

        viewTouchpad = findViewById(R.id.viewTouchpad);

        textIpAddress.setText(ipAddress);
        textPort.setText(Integer.toString(port));

        buttonOnOff.setOnClickListener(v -> toggleOnOff());
        buttonSettings.setOnClickListener(v -> finish());
        buttonLeftClick.setOnClickListener(v -> clickLeftMouse());

        viewTouchpad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int dx = x - previousX;
                int dy = y - previousY;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updateTouchpad(dx, dy);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - startTime < clickTimeMillis) clickLeftMouse();
                        break;
                }

                previousX = x;
                previousY = y;
                return true;
            }
        });
    }

    private void toggleOnOff()
    {
        isSendingData = !isSendingData;

        String value = isSendingData ? "ON" : "OFF";
        buttonOnOff.setText(value);
    }

    private void clickLeftMouse() {
        if (!isSendingData) return;

        SendUdpPacketTask sendUdpPacketTask = new SendUdpPacketTask(ipAddress, port);
        sendUdpPacketTask.execute();
    }

    private void updateTouchpad(int x, int y)
    {
        if (!isSendingData) return;

        SendUdpPacketTask sendUdpPacketTask = new SendUdpPacketTask(ipAddress, port, x, y);
        sendUdpPacketTask.execute();
    }
}