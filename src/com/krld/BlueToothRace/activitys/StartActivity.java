package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.krld.BlueToothRace.GameServer;
import com.krld.BlueToothRace.R;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by 3 on 02.02.14.
 */
public class StartActivity extends Activity {
    private Button startServer;
    private Button startClient;
    public static Socket connectedSocket;
    private GameServer gameServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        startServer = (Button) findViewById(R.id.startServerButton);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServerAndConnectHim();

            }
        });

        startClient = (Button) findViewById(R.id.startClientButton);
        startClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StartActivity.this, FindServerActivity.class);
                StartActivity.this.startActivity(myIntent);
            }
        });
    }

    private void startServerAndConnectHim() {
        if (gameServer == null) {
            gameServer = new GameServer();
            gameServer.init();
            gameServer.run();
        }

        new AsyncConnectToServer().execute();


    }


    public class AsyncConnectToServer extends AsyncTask<String, Void, Socket> {

        @Override
        protected Socket doInBackground(String... strings) {
            // String ip = strings[0];


            String ip = "127.0.0.1";
            Socket socket = null;
            try {
              //  ip = InetAddress.getLocalHost().toString();
             /*   if (socketClient != null && socketClient.isConnected()) {
                    Log.d(TAG, "try close socketClient");
                    socketClient.close();
                }          */
                Log.d(ServerActivity.TAG, "Try connecting: " + ip);
                socket = new Socket(ip, ServerActivity.SERVER_SOCKETY_PORT);
                Log.d(ServerActivity.TAG, "succesefull");
                Log.d(ServerActivity.TAG, "send create car request");
            } catch (IOException e) {
                Log.e(ServerActivity.TAG, "Error connecting: " + ip);
                // showToast("Error connecting: " + ip + " " + e.getMessage());
                e.printStackTrace();
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if (socket != null) {
                Intent myIntent = new Intent(StartActivity.this, ClientActivity.class);
                myIntent.putExtra("from", "StartActivity");
                connectedSocket = socket;
                showToast("connected");
                StartActivity.this.startActivity(myIntent);
            } else {
                showToast("Not connected!");
            }
        }
    }

    private void showToast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
