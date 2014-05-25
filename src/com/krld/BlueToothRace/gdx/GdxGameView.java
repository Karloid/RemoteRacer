package com.krld.BlueToothRace.gdx;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.krld.BlueToothRace.ProtocolMessages;
import com.krld.BlueToothRace.R;
import com.krld.BlueToothRace.activitys.ServerActivity;
import com.krld.BlueToothRace.model.*;
import com.krld.BlueToothRace.views.GameView;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 5/25/2014.
 */
public class GdxGameView extends ApplicationAdapter {
    public static final int MAGIC_HEIGHT = 720;
    private GdxClientLauncher activity;
    private GdxWorldRenderer gdxWorldRenderer;
    private boolean firstRender = true;

    public void setGame(Game game) {
        this.game = game;
    }

    SpriteBatch batch;
    private BitmapFont font;
    private MyInputProcessor inputProcessor;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("rotorBoyShadow.fnt"),
                Gdx.files.internal("rotorBoyShadow.png"), false);
        font.setColor(Color.WHITE);
        font.scale(1f);
        initInputProcessor();
        if (firstRender) {
            initGameAndRenderer();
            firstRender = false;
        }

        //   game.runGameLoop();
    }

    private void initGameAndRenderer() {
        initGame();
        initGdxWorldRenderer();
    }

    private void initGdxWorldRenderer() {
        gdxWorldRenderer = new GdxWorldRenderer();
        gdxWorldRenderer.setGame(game);
        gdxWorldRenderer.init();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        gdxWorldRenderer.drawGame(batch);
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, MAGIC_HEIGHT - 10);
        batch.end();
    }


    public void setInputProcessor(MyInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public void initInputProcessor() {
        inputProcessor = new MyInputProcessor();
        inputProcessor.setGdxGameView(this);
        Gdx.input.setInputProcessor(inputProcessor);
    }


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
    private boolean paused = false;
    private Thread syncWithServerThread;
    private long mainCarId;


    protected void initGame() {


        sendMessage(ProtocolMessages.CLIENT_REQUEST_CREATE_CAR);
        sendMessage(ProtocolMessages.CLIENT_REQUEST_CARS);

        initGameOld();

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
//                    gameView.postInvalidate();
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

    private void initGameOld() {
        //  final LinearLayout layout = (LinearLayout) findViewById(R.id.clientlayoutgameview);
        game = new Game(getActivity());
        // gameView = new GameView(this, game);
        //    layout.addView(gameView);
        //   gameView.setLayoutParams(new LinearLayout.LayoutParams(ServerActivity.VIEW_WIDTH, ServerActivity.VIEW_HEIGHT));
    }


    public boolean sendMessage(String message) {
        //     Log.i(ServerActivity.TAG, "Try send message from client");
        if (socket != null && !socket.isClosed()) {
            try {
                //           Log.e(ServerActivity.TAG, "Try send: " + message + " to socket");
                if (out == null) {
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                }
                out.append(message + "\n");
                out.flush();
                return true;
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

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setActivity(GdxClientLauncher activity) {
        this.activity = activity;
    }

    public GdxClientLauncher getActivity() {
        return activity;
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

            double speed = (Double) carMap.get(ProtocolMessages.CAR_SPEED);
            double angle = ((Double) carMap.get(ProtocolMessages.CAR_ANGLE));
            double turnAmount = (Double) carMap.get(ProtocolMessages.CAR_TURN_AMOUNT);

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

                double speed = (Double) carMap.get(ProtocolMessages.CAR_SPEED);
                double angle = ((Double) carMap.get(ProtocolMessages.CAR_ANGLE));
                double turnAmount = (Double) carMap.get(ProtocolMessages.CAR_TURN_AMOUNT);

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
//                gameView.postInvalidate();
                //      Log.d(ServerActivity.TAG, "carMap x: " + carMap.get("x") + " y: " + carMap.get("y"));
            }
        }
    }
}
