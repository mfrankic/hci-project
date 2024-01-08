package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MouseActivity extends AppCompatActivity {

    String ipAddress;
    int port;

    Button buttonOnOff;
    Button buttonSettings;
    Button buttonLeftClick;

    TextView textIpAddress;
    TextView textPort;

    boolean isSendingData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        Intent intent = getIntent();

        ipAddress = intent.getStringExtra("IP_ADDRESS");
        if (ipAddress == null) ipAddress = DefaultValues.IpAddress;

        port = intent.getIntExtra("PORT", DefaultValues.Port);

        textIpAddress = findViewById(R.id.textViewIpAddress);
        textPort = findViewById(R.id.textViewPort);

        buttonOnOff = findViewById(R.id.buttonTurnOnOff);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLeftClick = findViewById(R.id.buttonLeftClick);

        buttonOnOff.setOnClickListener(v -> toggleOnOff());
        buttonSettings.setOnClickListener(v -> finish());

        buttonLeftClick.setOnClickListener(v -> clickLeftMouse());
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
}