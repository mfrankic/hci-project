package com.example.client;

import android.content.Intent;
import android.net.InetAddresses;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    String ipAddress = DefaultValues.IpAddress;
    int port = DefaultValues.Port;

    EditText editTextAddress;
    EditText editTextPort;
    Button buttonSetDefault;
    Button buttonSetServerInfo;
    Button buttonStart;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextAddress = findViewById(R.id.editTextIpAddress);
        editTextPort = findViewById(R.id.editTextPort);

        buttonSetDefault = findViewById(R.id.buttonSetDefault);
        buttonSetServerInfo = findViewById(R.id.buttonSetServer);
        buttonStart = findViewById(R.id.buttonStart);

        radioGroup = findViewById(R.id.radioGroupMode);

        buttonSetDefault.setOnClickListener(v -> setDefaultValues());
        buttonSetServerInfo.setOnClickListener(v -> setServer());
        buttonStart.setOnClickListener(v -> startMode());

        setDefaultValues();
    }

    private void setDefaultValues()
    {
        ipAddress = DefaultValues.IpAddress;
        port = DefaultValues.Port;

        editTextAddress.setText(ipAddress);
        editTextPort.setText(Integer.toString(port));
    }

    private void setServer()
    {
        String ipText = editTextAddress.getText().toString();
        String portText = editTextPort.getText().toString();

        if (InetAddresses.isNumericAddress(ipText))
        {
            ipAddress = ipText;
        }
        else {
            editTextAddress.setText(ipAddress);
            Toast.makeText(this, "IP Address is invalid", Toast.LENGTH_LONG).show();
        }

        try{
            int portCurrent = Integer.parseInt(portText);
            if (portCurrent >= 0 && portCurrent <= 65535)
            {
                port = portCurrent;
            }
            else {
                editTextPort.setText(Integer.toString(port));
                Toast.makeText(this, "Port is invalid", Toast.LENGTH_LONG).show();
            }
        }
        catch (NumberFormatException e)
        {
            editTextPort.setText(Integer.toString(port));
            Toast.makeText(this, "Port is invalid", Toast.LENGTH_LONG).show();
        }
    }

    private void startMode()
    {
        Intent intent;

        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        switch (radioButton.getText().toString()) {
            case "Touchpad":
                intent = new Intent(this, TouchpadActivity.class);
                break;

            case "Mouse":
                intent = new Intent(this, MouseActivity.class);
                break;

            default:
                intent = null;
                break;
        }

        if (intent == null) return;

        intent.putExtra("IP_ADDRESS", ipAddress);
        intent.putExtra("PORT", port);
        startActivity(intent);
    }
}