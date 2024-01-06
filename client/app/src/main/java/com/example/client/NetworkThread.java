package com.example.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkThread extends Thread {
    private Socket socket;
    private PrintWriter out;

    @Override
    public void run() {
        try {
            socket = new Socket("192.168.1.102", 5382);
            out = new PrintWriter(socket.getOutputStream(), true);

            while (!socket.isClosed()) {
                // do nothing
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendSensorData(String data) {
        if (out != null && !out.checkError()) {
            out.println(data);
        }
    }
}

