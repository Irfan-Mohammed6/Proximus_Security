package com.example.proximussecuritysystem;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    Button tonext;
    EditText pass;
    String passwd = "enter";
    String entpss;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tonext = findViewById(R.id.button);
        tonext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pass = findViewById(R.id.Password);
                entpss = pass.getText().toString();

                if (entpss.equals(" ")) {

                    Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (entpss.equals(passwd))

                    {
                        Intent i = new Intent(MainActivity.this, LK_UK_Status.class);
                        startActivity(i);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}