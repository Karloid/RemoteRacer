package com.krld.BlueToothRace.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;
import com.krld.BlueToothRace.model.*;
import com.krld.BlueToothRace.views.GameView;

import java.io.*;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 4/9/2014.
 */


public class StandartClientActivity extends Activity {

    private static final long DELAY = 30;
    private static final long SYNC_DELAY = 150;
    private ImageButton increaseSpeedButton;
    private ImageButton decreaseSpeedButton;
    private ImageButton turnLeftButton;
    private ImageButton turnRightButton;
    private Socket socket;
    private ConnectionInputHandler connectionInputHandler;
    private Thread connectionInputHandlerThread;
    private BufferedWriter out;
    private Game game;
    private Thread localGameRunner;
    private GameView gameView;
    private boolean paused = false;
    private Thread syncWithServerThread;
    private long mainCarId;


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

        startLocalGameLoop();
        startSyncWithServerLoop();


    }

    private void startSyncWithServerLoop() {
        syncWithServerThread = new Thread(new Runnable() {
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
                    sendMessage(ProtocolMessages.CLIENT_REQUEST_CARS);
                    //Log.e("CAR", "UPDATE");
                    try {
                        Thread.sleep(SYNC_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        syncWithServerThread.start();
    }

    private void startLocalGameLoop() {
        localGameRunner = new Thread(new Runnable() {
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
        localGameRunner.start();
    }

    private void initGameView() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.clientlayoutgameview);
        game = new Game(this);
        gameView = new GameView(this, game);
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

    private boolean sendMessage(String message) {
   //     Log.i(ServerActivity.TAG, "Try send message from client");
        if (socket != null && !socket.isClosed()) {
            try {
     //           Log.e(ServerActivity.TAG, "Try send: " + message + " to socket");
                if (out == null) {
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                }
                out.append(message + "\n");
                out.flush();
                return  true;
                //    out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(ServerActivity.TAG, "Send message FAILED:" + e.getMessage());
            }
        } else {
            Log.e(ServerActivity.TAG, "socket is null or closed");
        }
        return false;
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
                        handleServerMessage(str);
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

        private void handleServerMessage(String str) {
            Log.d(ServerActivity.TAG, "client received message: " + str);
            if (str.equals(ProtocolMessages.OK)) {
                return;
            }
            Map root = new Gson().fromJson(str, Map.class);
            String header = (String) root.get(ProtocolMessages.HEADER);
            if (header.equals(ProtocolMessages.HEADER_GET_CAR)) {
                handleCarsMessage(root);
            } else if (header.equals(ProtocolMessages.HEADER_NEW_CAR)) {
                handleNewCarMessage(root);
            }
        }

        private void handleNewCarMessage(Map carMap) {
            carMap = (Map) carMap.get(ProtocolMessages.CAR);
            long carId = Math.round((Double) carMap.get("id"));
            Integer x = ((Double) carMap.get("x")).intValue();
            Integer y = ((Double) carMap.get("y")).intValue();

            double speed = (Double)carMap.get(ProtocolMessages.CAR_SPEED);
            double angle = ((Double)carMap.get(ProtocolMessages.CAR_ANGLE));
            double turnAmount = (Double)carMap.get(ProtocolMessages.CAR_TURN_AMOUNT);

            Car mainCar = new Car(x, y, game);
            mainCar.setId(carId);
            mainCar.setSpeed(speed);
            mainCar.setAngle((float) angle);
            mainCar.setTurnAmount((float) turnAmount);
            mainCar.setSpeedState(SpeedStates.valueOf((String) carMap.get(ProtocolMessages.CAR_SPEED_STATE)));
            mainCar.setTurnState(TurnStates.valueOf((String) carMap.get(ProtocolMessages.CAR_TURN_STATE)));
            mainCarId = mainCar.getId();
            game.addNewCarFromServer(mainCar);
            game.setMainCar(mainCar);
        }

        private void handleCarsMessage(Map root) {
            for (Map carMap : (List<Map>) root.get(ProtocolMessages.CARS)) {
                long carId = Math.round((Double) carMap.get("id"));
                Car car = game.getCarById(carId);
                Double x = ((Double) carMap.get(ProtocolMessages.CAR_POS_X));
                Double y = ((Double) carMap.get(ProtocolMessages.CAR_POS_Y));

                double speed = (Double)carMap.get(ProtocolMessages.CAR_SPEED);
                double angle = ((Double)carMap.get(ProtocolMessages.CAR_ANGLE));
                double turnAmount = (Double)carMap.get(ProtocolMessages.CAR_TURN_AMOUNT);

                if (car != null) {
                    car.pos = new Point(x, y);
                    car.setSpeed(speed);
                    car.setAngle((float) angle);
                    car.setTurnAmount((float) turnAmount);
                    car.setSpeedState(SpeedStates.valueOf((String) carMap.get(ProtocolMessages.CAR_SPEED_STATE)));
                    car.setTurnState(TurnStates.valueOf((String) carMap.get(ProtocolMessages.CAR_TURN_STATE)));
                } else {
                    car = new Car(x, y, game);
                    car.setId(carId);
                    car.setSpeed(speed);
                    car.setAngle((float) angle);
                    car.setTurnAmount((float) turnAmount);
                    car.setSpeedState(SpeedStates.valueOf((String) carMap.get(ProtocolMessages.CAR_SPEED_STATE)));
                    car.setTurnState(TurnStates.valueOf((String) carMap.get(ProtocolMessages.CAR_TURN_STATE)));
                    game.addNewCarFromServer(car);
                }
                gameView.postInvalidate();
          //      Log.d(ServerActivity.TAG, "carMap x: " + carMap.get("x") + " y: " + carMap.get("y"));
            }
        }
    }
}
