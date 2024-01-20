package com.example.client;

import android.os.AsyncTask;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class SendUdpPacketTask extends AsyncTask<Void, Void, Void> {

    private final String serverAddress;
    private final int serverPort;
    private final byte[] data;

    public SendUdpPacketTask(String serverAddress, int serverPort, int x, int y) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.data = getData(x, y, false);
    }

    public SendUdpPacketTask(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.data = getData(0, 0, true);
    }

    private byte[] getData(int x, int y, boolean clicked)
    {
        return ByteBuffer.allocate(5)
                .putShort((short) x)
                .putShort((short) y)
                .put((byte) (clicked ? 1 : 0))
                .array();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverAddress), serverPort);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}