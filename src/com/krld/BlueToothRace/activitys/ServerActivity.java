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

public class ServerActivity extends Activity {
    public static final int DELAY = 30;
    public static final int VIEW_WIDTH = 760;
    public static final int VIEW_HEIGHT = 950;
    public static final int SERVER_SOCKETY_PORT = 7777;
    public static final String TAG = "MY_RACE";
    private Game game;
    private boolean paused;
    private GameView gameView;
    private Thread runner;
    private ServerSocket server;
    private Runnable serverRunnable;
    private Thread serverSocketThread;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        game = new Game();
        gameView = new GameView(this, game);

        layout.addView(gameView);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(VIEW_WIDTH, VIEW_HEIGHT));

        initControlButtons();

        paused = false;

        startRunnerThread();
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
                    game.getLocalCar().increaseSpeed();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    game.getLocalCar().stillSpeed();
                }
                return false;
            }
        });

        decreaseSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    game.getLocalCar().decreaseSpeed();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    game.getLocalCar().stillSpeed();
                }
                return false;
            }
        });

        turnLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    game.getLocalCar().turnLeft();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    game.getLocalCar().noTurn();
                }
                return false;
            }
        });
        turnRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    game.getLocalCar().turnRight();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    game.getLocalCar().noTurn();
                }
                return false;
            }
        });
    }

    private void startSocketServer() {

        serverRunnable = new ServerRunnable();
        serverSocketThread = new Thread(serverRunnable);
        serverSocketThread.start();
    }

    private void startRunnerThread() {
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
                    game.update();
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
        stopSocketServer();
    }

    private void stopSocketServer() {
        serverSocketThread.interrupt();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;
        startSocketServer();
    }

    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                server = new ServerSocket(SERVER_SOCKETY_PORT);
                Log.i(TAG, "Server socket started at: " + SERVER_SOCKETY_PORT);

                while (true) {
                    Log.i(TAG, "waiting connections...: ");
                    Socket socket = server.accept();
                    Log.i(TAG, "get connection from: " + socket.getInetAddress());
                    new Thread(new ConnectionHandler(socket)).start();

                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class ConnectionHandler implements Runnable {
        private final Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, "New input connection handler!");

                Car car = game.createNewCar();


                BufferedReader in = null;

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (Utils.userIsAMonkey()) {
                    String str = in.readLine();


                    if (str != null) {
                        Log.i(TAG, "Received message: " + str);

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
                        break;
                    }
                }
                in.close();
                socket.close();
                Log.i(TAG, "Connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
