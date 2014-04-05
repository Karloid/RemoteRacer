package com.krld.BlueToothRace.model;

/**
 * Created by 3 on 28.01.14.
 */
public class Point {
    private  int y;
    private  int x;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
