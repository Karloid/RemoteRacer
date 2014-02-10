package com.krld.BlueToothRace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

        ImageButton increaseSpeedButton = (ImageButton) findViewById(R.id.increaseSpeedButton);
        ImageButton decreaseSpeedButton = (ImageButton) findViewById(R.id.decreaseSpeedButton);

        ImageButton turnLeftButton = (ImageButton) findViewById(R.id.turnLeftButton);
        ImageButton turnRightButton = (ImageButton) findViewById(R.id.turnRightButton);

        startSocketServer();
        increaseSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    game.getCar().increaseSpeed();
                }
            }
        });

        decreaseSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    game.getCar().decreaseSpeed();
                }
            }
        });

        turnLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    game.getCar().turnLeft();
                }
            }
        });
        turnRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    game.getCar().turnRight();
                }
            }
        });

        paused = false;

        startRunnerThread();
    }

    private void startSocketServer() {
        serverRunnable = new ServerRunnable();
        new Thread(serverRunnable).start();
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
                BufferedReader in = null;

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (Utils.userIsAMonkey()) {
                    String str = in.readLine();

                    Log.i(TAG, "Received message: " + str);
                    if (str != null) {
                        if (str.equals("w")) {
                            game.getCar().increaseSpeed();
                        }
                        if (str.equals("s")) {
                            game.getCar().decreaseSpeed();
                        }
                        if (str.equals("a")) {
                            game.getCar().turnLeft();
                        }
                        if (str.equals("d")) {
                            game.getCar().turnRight();
                        }
                    }
                }
                in.close();
                socket.close();
                Log.i(TAG, "Connections closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
