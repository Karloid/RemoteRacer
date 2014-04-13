package com.krld.BlueToothRace;

import android.util.Log;
import com.google.gson.Gson;
import com.krld.BlueToothRace.activitys.ServerActivity;
import com.krld.BlueToothRace.model.Car;
import com.krld.BlueToothRace.model.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrey on 4/10/2014.
 */
public class GameServer {

    public static final int DELAY = 30;
    public static final int VIEW_WIDTH = 760;
    public static final int VIEW_HEIGHT = 950;
    public static final int SERVER_SOCKETY_PORT = 7777;
    public static final String TAG = "MY_RACE";
    private boolean paused;
    private static Game gameMain;
    private static Thread runner;
    private static ServerSocket serverSocket;
    private static Runnable serverRunnable;
    private static Thread serverSocketThread;
    private static List<Thread> connections;

    public Socket getLocalSocket() {
        return null;

    }


    public void init() {

        if (gameMain == null) {
            gameMain = new Game();
        }

        startSocketServer();
        startGameLoopThread();


        paused = false;

    }

    public void run() {

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
                    gameMain.update();
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
        private BufferedWriter out;

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
                        car = gameMain.createNewCar();
                        id = car.getId();
                        log("Created car");
                        sendNewCarId(car);
                        break;
                    } else {
                        log("Break connection on wait user create car");
                        break;
                    }
                }

                while (true) {
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
                        } else if (str.equals(ProtocolMessages.CLIENT_REQUEST_CARS)) {
                            sendCarsToClient(car);
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

        private void sendNewCarId(Car car) {
            Gson gson = new Gson();
            String message = "{";
            message += " \"" + ProtocolMessages.HEADER +  "\" : \""  + ProtocolMessages.HEADER_NEW_CAR + "\",";

            HashMap<String, Object> carParams = new HashMap<String, Object>();
            carParams.put(ProtocolMessages.CAR_ID, car.getId());
            carParams.put(ProtocolMessages.CAR_POS_X, car.pos.getX());
            carParams.put(ProtocolMessages.CAR_POS_Y, car.pos.getY());
            carParams.put(ProtocolMessages.CAR_SPEED, car.getSpeed());
            carParams.put(ProtocolMessages.CAR_ANGLE, car.getAngle());
            carParams.put(ProtocolMessages.CAR_TURN_AMOUNT, car.getTurnAmount());

            carParams.put(ProtocolMessages.CAR_SPEED_STATE, car.getSpeedState());
            carParams.put(ProtocolMessages.CAR_TURN_STATE, car.getTurnState());
            message += "\"" + ProtocolMessages.CAR + "\" : " + gson.toJson(carParams);
            message += "}";
            sendMessage(message);
        }

        private void sendCarsToClient(Car car) {
            Gson gson = new Gson();
            String message;
            message = "{";
            message += " \"" + ProtocolMessages.HEADER + "\" : \"" + ProtocolMessages.CLIENT_REQUEST_CARS + "\",";
            message += " \"" + ProtocolMessages.CARS + "\" : ";
            ArrayList<HashMap<String, Object>> cars = new ArrayList<HashMap<String, Object>>();
            for (Car itCar : gameMain.getCars()) {
                HashMap<String, Object> carParams = new HashMap<String, Object>();
                carParams.put(ProtocolMessages.CAR_ID, itCar.getId());
                carParams.put(ProtocolMessages.CAR_POS_X, itCar.pos.getX());
                carParams.put(ProtocolMessages.CAR_POS_Y, itCar.pos.getY());
                carParams.put(ProtocolMessages.CAR_SPEED, itCar.getSpeed());
                carParams.put(ProtocolMessages.CAR_ANGLE, itCar.getAngle());
                carParams.put(ProtocolMessages.CAR_TURN_AMOUNT, itCar.getTurnAmount());

                carParams.put(ProtocolMessages.CAR_SPEED_STATE, itCar.getSpeedState());
                carParams.put(ProtocolMessages.CAR_TURN_STATE, itCar.getTurnState());

                cars.add(carParams);

            }
            message += gson.toJson(cars);
            message += "}";
            sendMessage(message);
        }

        private void log(String s) {
            Log.d(ServerActivity.TAG, "ConnectionHandler " + id + " : " + s);
        }

        private void sendMessage(String message) {
            Log.i(ServerActivity.TAG, "Try send message: " + message);
            if (socket != null && !socket.isClosed()) {
                // BufferedWriter out = null;
                try {
                    Log.d(ServerActivity.TAG, "Try send: " + message + " to socket");
                    if (out == null) {
                        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    }
                    out.append(message + "\n");
                    out.flush();
                    //           out.close();
                } catch (IOException e) {
                    Log.e(ServerActivity.TAG, "Send message FAILED:" + e.getMessage());
                }
            } else {
                Log.e(ServerActivity.TAG, " socket is null");
            }
        }
    }

}
