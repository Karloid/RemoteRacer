package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.model.Car;
import com.krld.BlueToothRace.views.GameView;
import com.krld.BlueToothRace.R;
import com.krld.BlueToothRace.Utils;
import com.krld.BlueToothRace.model.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
@Deprecated
public class ServerActivity extends Activity {
    public static final int DELAY = 30;
    public static final int VIEW_WIDTH = 760;
    public static final int VIEW_HEIGHT = 950;
    public static final int SERVER_SOCKETY_PORT = 7777;
    public static final String TAG = "MY_RACE";
    private boolean paused;
    private GameView gameView;
    private static Game gameServer;
    private static Thread runner;
    private static ServerSocket serverSocket;
    private static Runnable serverRunnable;
    private static Thread serverSocketThread;
    private static List<Thread> connections;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);

        if (gameServer == null) {
            gameServer = new Game();
        }
        gameView = new GameView(this, gameServer);

        startSocketServer();
        startGameLoopThread();


        final LinearLayout layout = (LinearLayout) findViewById(R.id.clientlayout); // error

        layout.addView(gameView);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(VIEW_WIDTH, VIEW_HEIGHT));

        initControlButtons();

        paused = false;


    }

    private void initControlButtons() {
        ImageButton increaseSpeedButton = (ImageButton) findViewById(R.id.increaseSpeedButton);
        ImageButton decreaseSpeedButton = (ImageButton) findViewById(R.id.decreaseSpeedButton);

        ImageButton turnLeftButton = (ImageButton) findViewById(R.id.turnLeftButton);
        ImageButton turnRightButton = (ImageButton) findViewById(R.id.turnRightButton);


        increaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameServer.getLocalCar().increaseSpeed();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameServer.getLocalCar().stillSpeed();
                }
                return false;
            }
        });

        decreaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameServer.getLocalCar().decreaseSpeed();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameServer.getLocalCar().stillSpeed();
                }
                return false;
            }
        });

        turnLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameServer.getLocalCar().turnLeft();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameServer.getLocalCar().noTurn();
                }
                return false;
            }
        });
        turnRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameServer.getLocalCar().turnRight();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameServer.getLocalCar().noTurn();
                }
                return false;
            }
        });
    }

    private void startSocketServer() {

        if (serverSocketThread == null) {
            serverRunnable = new ServerRunnable();
            serverSocketThread = new Thread(serverRunnable);
            serverSocketThread.start();
        }
    }

    private void startGameLoopThread() {
        runner = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (paused) {
                        try {
                            Thread.sleep(1000);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    gameServer.update();
                    gameView.postInvalidate();
                    //Log.e("CAR", "UPDATE");
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        runner.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
        //    stopSocketServer();
    }

    private void stopSocketServer() {
        serverSocketThread.interrupt();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;
    }

    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                if (serverSocket != null) {
                    Log.d(TAG, " try close serverSocket!");
                    serverSocket.close();
                    //   serverSocket.
                    Log.d(TAG, "serverSoscket is closed");
                }
                connections = new ArrayList<Thread>();
                serverSocket = new ServerSocket(SERVER_SOCKETY_PORT);
                Log.d(TAG, "Server socket started at: " + SERVER_SOCKETY_PORT);

                while (true) {
                    Log.i(TAG, "waiting connections...: ");
                    Socket socket = serverSocket.accept();
                    Log.i(TAG, "get connection from: " + socket.getInetAddress());
                    //
                    Thread connectionHandler = new Thread(new ConnectionHandler(socket));
                    connections.add(connectionHandler);
                    connectionHandler.start();

                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                try {
                    serverSocket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class ConnectionHandler implements Runnable {
        private final Socket socket;


        private long id;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
            id = -1;
        }

        public long getId() {
            return id;
        }

        @Override
        public void run() {
            try {
                log("New input connection handler!");

                Car car = null;


                BufferedReader in = null;

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String str = in.readLine();
                    if (str != null && str.equals(ProtocolMessages.CLIENT_REQUEST_CREATE_CAR)) {
                        car = gameServer.createNewCar();
                        id = car.getId();
                        log("Created car");
                        break;
                    } else {
                        log("Break connection on wait user create car");
                        break;
                    }
                }

                while (Utils.userIsAMonkey()) {
                    String str = in.readLine();


                    if (str != null) {
                        log("Received message: " + str);

                        if (str.equals(ProtocolMessages.INCREASE_SPEED)) {
                            car.increaseSpeed();
                        } else if (str.equals(ProtocolMessages.STILL_SPEED)) {
                            car.stillSpeed();
                        } else if (str.equals(ProtocolMessages.DECREASE_SPEED)) {
                            car.decreaseSpeed();
                        } else if (str.equals(ProtocolMessages.TURN_LEFT)) {
                            car.turnLeft();
                        } else if (str.equals(ProtocolMessages.TURN_RIGHT)) {
                            car.turnRight();
                        } else if (str.equals(ProtocolMessages.NO_TURN)) {
                            car.noTurn();
                        }
                    } else {
                        log("break connection on wait user next command");
                        break;
                    }
                }
                in.close();
                socket.close();
                log("Connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void log(String s) {
            Log.d(ServerActivity.TAG, "ConnectionHandler " + id + " : " + s);
        }
    }


}
