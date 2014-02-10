package com.krld.BlueToothRace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.*;
import java.net.Socket;

/**
 * Created by 3 on 03.02.14.
 */
public class ClientActivity extends Activity {
    private EditText ipAdressText;
    private Socket socketClient;

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
                                Log.i(ServerActivity.TAG, "Try connecting: "  + ipAdressText.getText().toString());
                                socketClient = new Socket(ipAdressText.getText().toString(), 7777);
                                Log.i(ServerActivity.TAG, "succesefull ");
                            } catch (IOException e) {
                                Log.e(ServerActivity.TAG, "Error connecting: " + ipAdressText.getText().toString());
                                e.printStackTrace();
                            }
                        }
                    }).start();



            }
        });
        ImageButton increaseSpeed = (ImageButton) findViewById(R.id.increaseSpeedClientButton);
        increaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(ServerActivity.TAG, "Click on increase speed");
                if ( socketClient != null && !socketClient.isClosed()) {
                    try {
                        Log.e(ServerActivity.TAG, "Try send W to socket");
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
                      /*  OutputStream outputStream = socketClient.getOutputStream();
                        outputStream.write("w");
                        outputStream.flush();*/
                        out.append("w\n");
//                        out.write("w");
                        out.flush();
                    } catch (IOException e) {
                        Log.e(ServerActivity.TAG, "Click on Increase speed FAILED:" + e.getMessage());

                    }
                } else {
                    Log.e(ServerActivity.TAG, "Click on Increase speed FAILED: socket is null");
                }

            }
        });
    }
}
