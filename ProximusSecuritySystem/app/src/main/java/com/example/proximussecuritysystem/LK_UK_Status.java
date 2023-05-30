package com.example.proximussecuritysystem;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LK_UK_Status extends AppCompatActivity {

    private final String DEVICE_ADDRESS = "FC:A8:9A:00:22:BA"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;

    private OutputStream outputStream;
    private InputStream inputStream;

    Thread thread;
    byte buffer[];

    boolean stopThread;
    boolean connected = false;
    String command;

    Button lock_state_btn, bluetooth_connect_btn, unlock_state_btn;

    TextView lock_state_text;
    ImageButton to_sensors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lkukstatus);


        lock_state_btn = (Button) findViewById(R.id.lock_state_btn);
        unlock_state_btn = (Button) findViewById(R.id.unlock_state_btn);
        bluetooth_connect_btn = (Button) findViewById(R.id.bluetooth_connect_btn);

        lock_state_text = (TextView) findViewById(R.id.lock_state_text);



        to_sensors = findViewById(R.id.nav_to_sensors);
        to_sensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tosens = new Intent(LK_UK_Status.this, Sensor_Readings.class);
                startActivity(tosens);
            }
        });



        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BTinit()) {
                    try {
                        BTconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    beginListenForData();

                    // The code below sends the number 3 to the Arduino asking it to send the current state of the door lock so the lock state icon can be updated accordingly

                    command = "3";

                    try {
                        outputStream.write(command.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        lock_state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connected == false) {
                    Toast.makeText(getApplicationContext(), "Please establish a connection with the bluetooth lock first", Toast.LENGTH_SHORT).show();
                } else {
                    lock_state_text.setText("Lock State: LOCKED");
                    command = "1";

                    try {
                        outputStream.write(command.getBytes()); // Sends the number 1 to the Arduino. For a detailed look at how the resulting command is handled, please see the Arduino Source Code
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        unlock_state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connected == false) {
                    Toast.makeText(getApplicationContext(), "Please establish a connection with the bluetooth lock first", Toast.LENGTH_SHORT).show();
                } else {
                    lock_state_text.setText("Lock State: UNLOCKED");
                    command = "2";

                    try {
                        outputStream.write(command.getBytes()); // Sends the number 1 to the Arduino. For a detailed look at how the resulting command is handled, please see the Arduino Source Code
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    void beginListenForData() // begins listening for any incoming data from the Arduino
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];

        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteCount = inputStream.available();

                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string = new String(rawBytes, "UTF-8");

                            handler.post(new Runnable() {
                                public void run() {
                                    if (string.equals("3")) {
                                        lock_state_text.setText("Lock State: LOCKED"); // Changes the lock state text
                                    } else if (string.equals("4")) {
                                        lock_state_text.setText("Lock State: UNLOCKED");

                                    }
                                }
                            });
                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    //Initializes bluetooth module
    @SuppressLint("MissingPermission")
    public boolean BTinit() {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission") Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    @SuppressLint("MissingPermission")
    public boolean BTconnect() throws IOException {


        socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
        socket.connect();

        Toast.makeText(getApplicationContext(),
                "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        connected = true;

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                inputStream = socket.getInputStream(); //gets the input stream of the socket
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

}
