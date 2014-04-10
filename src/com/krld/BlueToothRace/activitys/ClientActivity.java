package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;
import com.krld.BlueToothRace.model.Game;
import com.krld.BlueToothRace.views.GameView;

import java.io.*;
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
    private ConnectionInputHandler connectionInputHandler;
    private Thread connectionInputHandlerThread;
    private BufferedWriter out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        if (getIntent().getStringExtra("from").equals("FindServerActivity")) {
            socket = FindServerActivity.connectedSocket;
        } else if (getIntent().getStringExtra("from").equals("StartActivity")) {
            socket = StartActivity.connectedSocket;
        }

        sendMessage(ProtocolMessages.CLIENT_REQUEST_CREATE_CAR);
        sendMessage(ProtocolMessages.CLIENT_REQUEST_CARS);

        initControlButtons();
        initGameView();

        connectionInputHandler = new ConnectionInputHandler(socket);
        connectionInputHandlerThread = new Thread(connectionInputHandler);
        connectionInputHandlerThread.start();


    }

    private void initGameView() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.clientlayoutgameview);
        View gameView = new GameView(this, new Game());
        layout.addView(gameView);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(ServerActivity.VIEW_WIDTH, ServerActivity.VIEW_HEIGHT));
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
        Log.i(ServerActivity.TAG, "Try send message from client");
        if (socket != null && !socket.isClosed()) {
            try {
                Log.e(ServerActivity.TAG, "Try send: " + message + " to socket");
                if (out == null) {
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                }
                out.append(message + "\n");
                out.flush();
                //    out.close();
            } catch (IOException e) {
                Log.e(ServerActivity.TAG, "Send message FAILED:" + e.getMessage());
            }
        } else {
            Log.e(ServerActivity.TAG, "socket is null or closed");
        }
    }

    private class ConnectionInputHandler implements Runnable {
        public ConnectionInputHandler(Socket socket) {
        }

        @Override
        public void run() {
            try {
                Log.d(ServerActivity.TAG, "New input connection handler on client!");

                BufferedReader in = null;

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String str = in.readLine();
                    if (str != null) {
                        Log.d(ServerActivity.TAG, "client received message: " + str);
                    } else {
                        Log.d(ServerActivity.TAG, "Break connection on str == null client handler");
                        break;
                    }
                }
                in.close();
                socket.close();
                Log.d(ServerActivity.TAG, "Connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
