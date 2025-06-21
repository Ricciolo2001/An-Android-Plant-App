package com.example.apppiantina.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.apppiantina.R;
import com.example.apppiantina.model.GardenModel;
import com.example.apppiantina.model.MoistureSensor;
import com.example.apppiantina.model.Piantina;

import java.util.List;

public class PlantBubbleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_bubble_activity);

        TextView infoText = findViewById(R.id.bubbleInfoText);

        String plantName = getIntent().getStringExtra("plant_name");
        if (plantName != null) {
            GardenModel model = GardenModel.getInstance();
            List<MoistureSensor> sensori = model.getSensoriPerPiantina(plantName);

            StringBuilder info = new StringBuilder();
            info.append("Piantina: ").append(plantName).append("\n\n");

            if (sensori.isEmpty()) {
                info.append("Nessun sensore collegato.");
            } else {
                for (MoistureSensor sensor : sensori) {
                    info.append("• ID: ").append(sensor.thingId).append("\n");
                    info.append("  - Umidità: ").append(sensor.values.soilMoisture).append("%\n\n");
                }
            }

            infoText.setText(info.toString());
        }
    }
}
