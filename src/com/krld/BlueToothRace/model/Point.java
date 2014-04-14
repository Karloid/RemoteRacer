package com.krld.BlueToothRace.model;

/**
 * Created by 3 on 28.01.14.
 */
public class Point {
    private double y;
    private double x;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public int getXIntValue() {
        return (int)x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getYIntValue() {
        return (int)y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
