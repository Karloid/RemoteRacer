package com.krld.BlueToothRace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 3 on 28.01.14.
 */
public class Game {

    public static final int FIELD_SIZE = 50;
    private final int cellSize = 64;
    public TileType[][] tiles;

    private Car localCar;
    private List<Car> cars;
    private long id = 0;
    private Car mainCar;

    public Game() {
        setLocalCar(new Car(0, 0, this));
        generateTiles();
        cars = new ArrayList<Car>();
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

    public Car getLocalCar() {
        return localCar;
    }

    public void setLocalCar(Car localCar) {
        this.localCar = localCar;
    }

    public void update() {
        localCar.update();
        for (Car car : cars) {
            car.update();
        }
    }

    public TileType[][] getTiles() {
        return tiles;
    }

    public int getCellSize() {
        return cellSize;
    }


    public Car createNewCar() {

        Car car = new Car(localCar.pos.getX(), localCar.pos.getY(), this);
        cars.add(car);
        return car;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public long getNextEntityId() {
        id++;
        return id;
    }

    public Car getCarById(long carId) {
        for (Car car : cars) {
            if (car.getId() == carId) {
                return car;
            }
        }
        return null;
    }

    public void addNewCarFromServer(Car car) {
        cars.add(car);
    }

    public Car getMainCar() {
        return mainCar;
    }

    public void setMainCar(Car mainCar) {
        this.mainCar = mainCar;
    }
}
