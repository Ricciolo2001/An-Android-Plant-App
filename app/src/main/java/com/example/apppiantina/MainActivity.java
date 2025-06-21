package com.example.apppiantina;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.graphics.Rect;
import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppiantina.model.GardenModel;
import com.example.apppiantina.model.MoistureSensor;
import com.example.apppiantina.model.Piantina;

public class MainActivity extends AppCompatActivity {

    private ProgressBar soilProgress;
    private EditText plantNameEditText;
    private SeekBar thresholdSeekBar;
    private TextView waterMoistureLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connects essential components to the ui data
        soilProgress = findViewById(R.id.soilProgress);
        plantNameEditText = findViewById(R.id.plantName);
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar);
        waterMoistureLevel = findViewById(R.id.waterMoistureLevel);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Persistency on device
        SharedPreferences prefs= getSharedPreferences("PlantPrefs", MODE_PRIVATE);

        // Loads name
        String savedName = prefs.getString("plantName", "Plant Name");
        plantNameEditText.setText(savedName);

        // Loads saved threshold
        int savedThreshold = prefs.getInt("threshold", 20);
        thresholdSeekBar.setProgress(savedThreshold);

        // Moisture
        waterMoistureLevel.setText("%");


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Name updater event
        plantNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // cioè: quando si perde il focus (es. chiude tastiera)
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("plantName", plantNameEditText.getText().toString());
                editor.apply();
            }
        });

        // Water warning update event
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterMoistureLevel.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int newThreshold = seekBar.getProgress();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("threshold", newThreshold);
                editor.apply();
                Toast.makeText(MainActivity.this, "Soglia salvata: " + newThreshold + "%", Toast.LENGTH_SHORT).show();

                waterMoistureLevel.setText("%");
            }
        });

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Piantina p=new Piantina("Mia pinatina");
        GardenModel.getInstance().aggiungiPiantina(p);

        UdpListener u = new UdpListener(this.getApplicationContext());
        u.run();
    }

    private void simulateHydration() {
        // Simula un valore ricevuto (es. 65%)
        int hydrationValue = 65;
        soilProgress.setProgress(hydrationValue);
        waterMoistureLevel.setText(hydrationValue+"%");

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus(); // Togli focus
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // Nascondi tastiera
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}
