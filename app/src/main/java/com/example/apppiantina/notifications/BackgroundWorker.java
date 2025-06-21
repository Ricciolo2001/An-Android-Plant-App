package com.example.apppiantina.notifications;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.apppiantina.model.GardenModel;
import com.example.apppiantina.model.MoistureSensor;
import com.example.apppiantina.model.Piantina;

import java.util.List;

public class BackgroundWorker extends Worker {
    private GardenModel model;
    private BubbleNotificationManager manager;
    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters params) throws Exception {
        super(context, params);
         model= GardenModel.getInstance();
         manager = BubbleNotificationManager.getInstance(this.getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {

        for (Piantina pianta : model.getPiantine()) {
            List<MoistureSensor> sensori = model.getSensoriPerPiantina(pianta.getName());

            for (MoistureSensor sensor : sensori) {
                if (sensor.values.soilMoisture < 50) {
                     manager.plantNotification(pianta);
                }
            }
        }
        return Result.success();
    }
}
