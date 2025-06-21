package com.example.apppiantina;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.apppiantina.model.GardenModel;
import com.example.apppiantina.model.MoistureSensor;

import com.example.apppiantina.notifications.BubbleNotificationManager;
import com.google.gson.Gson;


public class UdpListener extends Thread {

    private static final String TAG = "UdpListener";
    private Context context;

    private volatile boolean running = true;
    private Thread daemonThread;

    private final String listenAddress = "0.0.0.0";
    private final int port = 1235;

    public UdpListener(Context context) {
        this.context = context.getApplicationContext(); // Usa il context globale
    }

    public void stopListening() {
        running = false;
        if (daemonThread != null && daemonThread.isAlive()) {
            daemonThread.interrupt();
        }
    }



    @Override
    public void run() {
        daemonThread = new Thread(() -> {
            DatagramSocket socket = null;
            try {
                InetAddress address = InetAddress.getByName(listenAddress);
                socket = new DatagramSocket(port, address);
                socket.setSoTimeout(0); // blocca in ricezione finché non arriva qualcosa

                byte[] buffer = new byte[1024];

                GardenModel gardenModel = GardenModel.getInstance();

                while (running) {
                    try {

                        ///////////////////////////////////////////////////////////////////////////////
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String message = new String(packet.getData(), 0, packet.getLength());
                        //Log.d(TAG, "Ricevuto: " + message);

                        // Parsing Json message
                        try {
                            JSONObject json = new JSONObject(message);
                            Map<String, Object> data = jsonToMap(json);


                            // Per i valori annidati
                            Map<String, Object> values = (Map<String, Object>) data.get("values");

                            // Stampe per debug
//                            Log.d(TAG, "Parsed JSON: " + json.toString());
//                            Log.d(TAG, "Thing ID: " + data.get("thing_id"));
//                            Log.d(TAG, "Soil Moisture: " + values.get("soil_moisture"));

                            Gson gson = new Gson();
                            if (data.get("sensorType").equals("Soil Moisture")) {
                                MoistureSensor sensor = gson.fromJson(message, MoistureSensor.class);
                                gardenModel.aggiungiSensore(sensor);

                                SharedPreferences prefs = getSharedPreferences("PlantPrefs", MODE_PRIVATE);
                                if(sensor.values.humidity < prefs.getInt("threshold", 20))
                                    BubbleNotificationManager.getInstance(context).plantNotification(gardenModel.getPiantinaBySensor(sensor.thingId));

                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Errore parsing JSON: " + e.getMessage());
                        }


                        ///////////////////////////////////////////////////////////////////////////////
                    } catch (Exception e) {
                        if (running) {
                            Log.e(TAG, "Errore durante la ricezione: " + e.getMessage());
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore inizializzazione socket: " + e.getMessage());
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                Log.d(TAG, "UDP Listener terminato.");
            }
        });

        daemonThread.setDaemon(true);
        daemonThread.start();
    }


    private Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                map.put(key, jsonToMap((JSONObject) value)); // parsing ricorsivo
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

}
