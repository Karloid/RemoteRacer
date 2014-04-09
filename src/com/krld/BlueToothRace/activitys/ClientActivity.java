package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Andrey on 4/9/2014.
 */


public class ClientActivity extends Activity {

    private ImageButton increaseSpeedButton;
    private ImageButton decreaseSpeedButton;
    private ImageButton turnLeftButton;
    private ImageButton turnRightButton;
    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        socket = FindServerActivity.connectedSocket;

        sendMessage(ProtocolMessages.CREATE_CAR);

        initControlButtons();

    }

    private void initControlButtons() {
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
        if (socket != null && !socket.isClosed()) {
            BufferedWriter out = null;
            try {
                Log.e(ServerActivity.TAG, "Try send: " + message + " to socket");
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                out.append(message + "\n");
                out.flush();
            } catch (IOException e) {
                Log.e(ServerActivity.TAG, "Send message FAILED:" + e.getMessage());
            }
        } else {
            Log.e(ServerActivity.TAG, " socket is null");
        }
    }
}
