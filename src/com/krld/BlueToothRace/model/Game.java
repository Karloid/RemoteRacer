package com.krld.BlueToothRace.model;

/**
 * Created by 3 on 28.01.14.
 */
public class Game {

    public static final int FIELD_SIZE = 50;
    private final int cellSize = 64;
    public TileType[][] tiles;
    private Car remoteCar;

    private Car car;

    public Game() {
        setCar(new Car(0, 0));
        setRemoteCar(new Car(0, 100));
        generateTiles();
    }

    private void generateTiles() {
        tiles = new TileType[FIELD_SIZE][FIELD_SIZE];
        for (int x = 0; x < FIELD_SIZE; x++) {
            for (int y = 0; y < FIELD_SIZE; y++) {
                double random = Math.random();
                if (random > 0.75f) {
                    tiles[x][y] = TileType.GRASS1;
                } else if (random > 0.5f) {
                    tiles[x][y] = TileType.GRASS2;
                } else if (random > 0.25f) {
                    tiles[x][y] = TileType.GRASS3;
                } else if (random > 0f) {
                    tiles[x][y] = TileType.GRASS4;
                }
            }
        }
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void update() {
        car.update();
        remoteCar.update();
    }

    public TileType[][] getTiles() {
        return tiles;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Car getRemoteCar() {
        return remoteCar;
    }

    public void setRemoteCar(Car remoteCar) {
        this.remoteCar = remoteCar;
    }
}
