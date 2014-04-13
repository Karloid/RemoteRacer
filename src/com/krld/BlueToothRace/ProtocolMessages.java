package com.krld.BlueToothRace;

/**
 * Created by Andrey on 4/5/2014.
 */
public class ProtocolMessages {
    public static final String STILL_SPEED = "still_speed";
    public static final String INCREASE_SPEED = "increase_speed";
    public static final String DECREASE_SPEED = "decrease_speed";

    public static final String NO_TURN = "no_turn";
    public static final String TURN_LEFT = "turn_left";
    public static final String TURN_RIGHT = "turn_right";


    public static final String CLIENT_REQUEST_CREATE_CAR = "create_car";
    public static final String CLIENT_REQUEST_CARS = "get_cars";

    public static final String HEADER = "header";
    public static final String HEADER_NEW_CAR = "new_car";
    public static final String HEADER_GET_CAR = CLIENT_REQUEST_CARS;

    public static final String CARS = "cars";


    public static final String OK = "OK";
    public static final String CAR_SPEED = "speed";
    public static final String CAR_ANGLE = "angle";
    public static final String CAR_POS_X = "x";
    public static final String CAR_POS_Y = "y";
    public static final String CAR_TURN_AMOUNT = "turn_amount";
    public static final String CAR_SPEED_STATE = "speed_state";
    public static final String CAR_TURN_STATE = "turn_state";
    public static final String CAR_ID = "id";
    public static final String CAR = "car";
}
