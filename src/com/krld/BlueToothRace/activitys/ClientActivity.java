package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by 3 on 03.02.14.
 */
public class ClientActivity extends Activity {
    private static final String TAG = "MY_RACE";
    private EditText ipAdressText;
    private Socket socketClient;
    private ImageButton increaseSpeedButton;
    private ImageButton decreaseSpeedButton;
    private ImageButton turnLeftButton;
    private ImageButton turnRightButton;
    private Button refreshServerListButton;
    private ListView serverListView;
    private ProgressBar serverFindProgressBar;
    private List<String> findedServersIps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);


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

        initControlButtons();
    }

    private void connectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socketClient != null && socketClient.isConnected()) {
                        Log.d(TAG, "try close socketClient");
                        socketClient.close();
                    }
                    Log.d(ServerActivity.TAG, "Try connecting: " + ipAdressText.getText().toString());
                    socketClient = new Socket(ipAdressText.getText().toString(), ServerActivity.SERVER_SOCKETY_PORT);
                    Log.d(ServerActivity.TAG, "succesefull");
                    sendMessage(ProtocolMessages.CREATE_CAR);
                    Log.d(ServerActivity.TAG, "send create car request");
                } catch (IOException e) {
                    Log.e(ServerActivity.TAG, "Error connecting: " + ipAdressText.getText().toString());
                    showToast("Error connecting: " + ipAdressText.getText().toString() + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void findLocalServers() {
        if (serverFindProgressBar.getVisibility() == View.INVISIBLE)
            new AsyncFindLocalServers().execute();
    }

    private void sendMessage(String message) {
        Log.i(ServerActivity.TAG, "Click on increase speed");
        if (socketClient != null && !socketClient.isClosed()) {
            BufferedWriter out = null;
            try {
                Log.e(ServerActivity.TAG, "Try send: " + message + " to socket");
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


    private class AsyncFindLocalServers extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            serverFindProgressBar.setVisibility(View.VISIBLE);
            serverListView.setAdapter(new ArrayAdapter<String>(ClientActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
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
                socket.connect(new InetSocketAddress(ip, ServerActivity.SERVER_SOCKETY_PORT), timeout);
                socket.close();
                Log.d(ServerActivity.TAG, "FINDED SERVER: " + ip);
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
            serverListView.setAdapter(new ArrayAdapter<String>(ClientActivity.this, android.R.layout.simple_list_item_1, findedServersIps));
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
