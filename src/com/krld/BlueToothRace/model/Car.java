package com.krld.BlueToothRace.model;

import android.graphics.Color;

/**
 * Created by 3 on 28.01.14.
 */
public class Car {
    private static final double DEFAULT_SPEED_INCREASE_AMOUNT = 2;
    private static final double DEFAULT_TURN_AMOUNT = 8;
    public static final float REDUCE_TURN_AMOUNT = 0.8f;
    public static final int SPEED_LIMIT_ZERO = 1;
    private static final double MAX_SPEED_FORWARD = 12;
    private static final double MAX_SPEED_BACKWARD = -5;
    private static final double MAX_TURN_AMOUNT = 2;
    private final Game context;
    private final long id;
    private double turnAmountDelta;
    public Point pos;
    private double speed;
    private int color;
    private float angle;
    private SpeedStates speedState;
    private double speedIncreaseAmount;
    private double speedDecreaseAmount;
    private TurnStates turnState;
    private double turnAmount;

    public Car(int x, int y, Game context) {
        this.context = context;
        id = context.getNextEntityId();

        pos = new Point(x, y);
        angle = 0;
        speed = 10;
        color = Color.RED;
        speedState = SpeedStates.STILL;
        turnState = TurnStates.STILL;
        speedIncreaseAmount = DEFAULT_SPEED_INCREASE_AMOUNT;
        speedDecreaseAmount = DEFAULT_SPEED_INCREASE_AMOUNT;
        turnAmount = DEFAULT_TURN_AMOUNT;
        turnAmountDelta = 0;
    }

    public void update() {
        //  pos.setX((int) (pos.getX() + speed));
        calculateSpeed();
        calculateAngle();
        pos.setX((int) (pos.getX() + speed * Math.cos(angle / 180 * Math.PI)));
        pos.setY((int) (pos.getY() + speed * Math.sin(angle / 180 * Math.PI)));
        reduceSpeed();
        reduceAngleDeltaAmount();
        //    testAngle();
    }

    private void reduceAngleDeltaAmount() {
        turnAmountDelta *= REDUCE_TURN_AMOUNT;
        if (Math.abs(turnAmountDelta) < 0.3) {
            turnAmountDelta = 0;
        }
    }

    private void reduceSpeed() {
        speed *= 0.99f;
        if (Math.abs(speed) < SPEED_LIMIT_ZERO) {
            speed = 0;
        }
    }

    private void calculateSpeed() {
        if (speedState == SpeedStates.INCREASE) {
            speed += speedIncreaseAmount;
        } else if (speedState == SpeedStates.DECREASE) {
            speed -= speedDecreaseAmount;
        }
        if (speed > MAX_SPEED_FORWARD) {
            speed = MAX_SPEED_FORWARD;
        } else if (speed < MAX_SPEED_BACKWARD) {
            speed = MAX_SPEED_BACKWARD;
        }

    }


    private void calculateAngle() {

        if (turnState == TurnStates.LEFT) {
            turnAmountDelta -= turnAmount;
        } else if (turnState == TurnStates.RIGHT) {
            turnAmountDelta += turnAmount;
        }

        if (turnAmountDelta != 0) {
            turnAmountDelta = turnAmountDelta * (1 - Math.pow(Math.abs(speed) / MAX_SPEED_FORWARD, 3) * 0.5f);
        }

        if (turnAmountDelta > MAX_TURN_AMOUNT) {
            turnAmount = MAX_TURN_AMOUNT;
        } else if (turnAmount < -MAX_TURN_AMOUNT) {
            turnAmount = -MAX_TURN_AMOUNT;
        }


        if (speed == 0) {
            turnAmountDelta = 0;
        }

        angle += turnAmountDelta;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void increaseSpeed() {
        speedState = SpeedStates.INCREASE;
    }

    public void decreaseSpeed() {
        speedState = SpeedStates.DECREASE;
    }

    public void turnRight() {
        turnState = TurnStates.RIGHT;
    }


    public void turnLeft() {
        turnState = TurnStates.LEFT;
    }


    public void stillSpeed() {
        speedState = SpeedStates.STILL;
    }

    public void noTurn() {
        turnState = TurnStates.STILL;
    }

    public long getId() {
        return id;
    }
}
