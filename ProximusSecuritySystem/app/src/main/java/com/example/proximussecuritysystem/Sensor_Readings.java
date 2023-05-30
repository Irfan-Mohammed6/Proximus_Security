package com.example.proximussecuritysystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Sensor_Readings extends AppCompatActivity implements SensorEventListener {

    ImageButton to_LU;

    TextView proxval, accelsensval;
    private TextView txtStatus;

    SensorManager sensorManager;
    private boolean isObjectNearby = false;
    private boolean isZChanged = false;
    private int previousZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_readings);

        proxval = findViewById(R.id.proxsenval);
        accelsensval = findViewById(R.id.accelsenval);
        txtStatus = findViewById(R.id.txtStatus);
        to_LU = findViewById(R.id.nav_to_lkuk);
        to_LU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tolkuk = new Intent(Sensor_Readings.this, LK_UK_Status.class);
                startActivity(tolkuk);
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            Sensor proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


            if (accelSensor != null) {
                sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (proxSensor != null) {
                sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            Toast.makeText(this, "Sensor service not available", Toast.LENGTH_SHORT).show();
        }
    }


    // when sensor data changes (triggers)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // changing sensor values from float to int
            float x = sensorEvent.values[0];
            int intX = Math.round(x);
            float y = sensorEvent.values[1];
            int intY = Math.round(y);
            float z = sensorEvent.values[2];
            int intZ = Math.round(z);
            int currentZ = (int) sensorEvent.values[2];
            ((TextView) findViewById(R.id.accelsenval)).setText("Z: " + intZ);

            // checking if value is different (knocking)

            if ((currentZ == 1 || currentZ == 2 || currentZ == 0) && previousZ != currentZ) {
                isZChanged = true;
            } else {
                isZChanged = false;
            }
            previousZ = currentZ;

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            ((TextView) findViewById(R.id.proxsenval)).setText("values: " + sensorEvent.values[0]);
            // if object breaches proximity, setting the value to true
            if (sensorEvent.values[0] > 0) {
                isObjectNearby = false;
            } else {
                isObjectNearby = true;
            }
        }

        if (isObjectNearby == true && isZChanged == true) {
            ((TextView) findViewById(R.id.txtStatus)).setText("Someone inear your door is knocking");

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));


        } else if (isObjectNearby == true && isZChanged == false) {
            ((TextView) findViewById(R.id.txtStatus)).setText("Someone near your door");
        } else {
            ((TextView) findViewById(R.id.txtStatus)).setText("all good");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
