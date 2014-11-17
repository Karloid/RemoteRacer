package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.krld.BlueToothRace.Constants;
import com.krld.BlueToothRace.R;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by 3 on 03.02.14.
 */
public class FindServerActivity extends Activity {
    private static final String TAG = "MY_RACE";
    public static Socket connectedSocket;
    private EditText ipAdressText;
    private Socket socketClient;

    private Button refreshServerListButton;
    private ListView serverListView;
    private ProgressBar serverFindProgressBar;
    private List<String> findedServersIps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_server);


        initViews();

    }

    private void initViews() {

        Button connectButton = (Button) findViewById(R.id.connectClientButton);
        ipAdressText = (EditText) findViewById(R.id.ipAdressEdit);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });


        serverFindProgressBar = (ProgressBar) findViewById(R.id.serverFindProgressBar);

        serverListView = (ListView) findViewById(R.id.serverListView);

        refreshServerListButton = (Button) findViewById(R.id.refreshServerListButton);


        refreshServerListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findLocalServers();
            }
        });

        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                ipAdressText.setText(findedServersIps.get(index));
                connectToServer();

            }
        });

    }

    private void connectToServer() {
        new AsyncConnectToServer().execute(ipAdressText.getText().toString());
    }


    private void findLocalServers() {
        if (serverFindProgressBar.getVisibility() == View.INVISIBLE)
            new AsyncFindLocalServers().execute();
    }


    public class AsyncConnectToServer extends AsyncTask<String, Void, Socket> {

        @Override
        protected Socket doInBackground(String... strings) {
            String ip = strings[0];

            Socket socket = null;
            try {
                if (socketClient != null && socketClient.isConnected()) {
                    Log.d(TAG, "try close socketClient");
                    socketClient.close();
                }
                Log.d(Constants.TAG, "Try connecting: " + ip);
                socket = new Socket(ip, Constants.SERVER_SOCKET_PORT);
                Log.d(Constants.TAG, "succesefull");
                Log.d(Constants.TAG, "send create car request");
            } catch (IOException e) {
                Log.e(Constants.TAG, "Error connecting: " + ip);
              // showToast("Error connecting: " + ip + " " + e.getMessage());
                e.printStackTrace();
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if (socket != null) {
                Intent myIntent = new Intent(FindServerActivity.this, StandartClientActivity.class);
                myIntent.putExtra("from", "FindServerActivity");
                FindServerActivity.connectedSocket = socket;
                showToast("Connected");
                FindServerActivity.this.startActivity(myIntent);

            }
        }
    }

    private class AsyncFindLocalServers extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            serverFindProgressBar.setVisibility(View.VISIBLE);
            serverListView.setAdapter(new ArrayAdapter<String>(FindServerActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            final List<String> ips = new Vector<String>();
            for (int i = 0; i <= 255; i++) {
                final String ip = "192.168.43." + i;            //TODO fix this dirt
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkPort(ip)) {
                            ips.add(ip);
                        }
                    }
                }).start();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ips;
        }

        private boolean checkPort(String ip) {
            Socket socket = new Socket();
            int timeout = 100;
            try {
                socket.connect(new InetSocketAddress(ip, Constants.SERVER_SOCKET_PORT), timeout);
                socket.close();
                Log.d(Constants.TAG, "FINDED SERVER: " + ip);
                return true;
            } catch (IOException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(List<String> ips) {
            //   ips.add("192.168.2.2");
            serverFindProgressBar.setVisibility(View.INVISIBLE);
            findedServersIps = ips;
            serverListView.setAdapter(new ArrayAdapter<String>(FindServerActivity.this, android.R.layout.simple_list_item_1, findedServersIps));
            if (ips.isEmpty()) {
                showToast("No servers:(");
            } else {
                showToast("Server count: " + ips.size());
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
