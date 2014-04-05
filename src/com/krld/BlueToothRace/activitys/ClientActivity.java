package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;

import java.io.*;
import java.net.Socket;

/**
 * Created by 3 on 03.02.14.
 */
public class ClientActivity extends Activity {
    private EditText ipAdressText;
    private Socket socketClient;
    private ImageButton increaseSpeedButton;
    private ImageButton decreaseSpeedButton;
    private ImageButton turnLeftButton;
    private ImageButton turnRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        Button connectButton = (Button) findViewById(R.id.connectClientButton);
        ipAdressText = (EditText) findViewById(R.id.ipAdressEdit);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i(ServerActivity.TAG, "Try connecting: " + ipAdressText.getText().toString());
                            socketClient = new Socket(ipAdressText.getText().toString(), ServerActivity.SERVER_SOCKETY_PORT);
                            Log.i(ServerActivity.TAG, "succesefull ");
                        } catch (IOException e) {
                            Log.e(ServerActivity.TAG, "Error connecting: " + ipAdressText.getText().toString());
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
        increaseSpeedButton = (ImageButton) findViewById(R.id.increaseSpeedClientButton);

        increaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.INCREASE_SPEED);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.STILL_SPEED);
                }
                return false;
            }
        });

        decreaseSpeedButton = (ImageButton) findViewById(R.id.decreaseSpeedClientButton);

        decreaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.DECREASE_SPEED);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.STILL_SPEED);
                }
                return false;
            }
        });

        turnLeftButton = (ImageButton) findViewById(R.id.turnLeftClientButton);

        turnLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.TURN_LEFT);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.NO_TURN);
                }
                return false;
            }
        });

        turnRightButton = (ImageButton) findViewById(R.id.turnRightClientButton);

        turnRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendMessage(ProtocolMessages.TURN_RIGHT);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendMessage(ProtocolMessages.NO_TURN);
                }
                return false;
            }
        });




    }

    private void sendMessage(String message) {
        Log.i(ServerActivity.TAG, "Click on increase speed");
        if (socketClient != null && !socketClient.isClosed()) {
            BufferedWriter out = null;
            try {
                Log.e(ServerActivity.TAG, "Try send" + message + " to socket");
                out = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
                out.append(message + "\n");
                out.flush();
            } catch (IOException e) {
                Log.e(ServerActivity.TAG, "Send message FAILED:" + e.getMessage());
            }
        } else {
            Log.e(ServerActivity.TAG, "Click on Increase speed FAILED: socket is null");
        }
    }
}
