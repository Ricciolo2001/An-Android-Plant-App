package com.example.apppiantina;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener extends Thread {

    private int port = 1235;
    private boolean running = true;
    private Context context;

    public UdpListener(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (running) {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                // Salva su file
                saveLog(received);

                // Puoi anche aggiornare il valore con un Broadcast o Handler
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLog(String message) {
        File logFile = new File(context.getFilesDir(), "udp_log.txt");
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        running = false;
    }
}
