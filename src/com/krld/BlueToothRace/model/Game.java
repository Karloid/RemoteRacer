package com.krld.BlueToothRace.model;

import android.app.Activity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.krld.BlueToothRace.MapManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 3 on 28.01.14.
 */
public class Game {

    private final int cellSize = 64;
    private  Activity activity;

    private List<Car> cars;
    private long id = 0;
    private Car mainCar;
    private Point startPoint;
    private MapManager mapManager;
    private int[][] tiles;

    public Game(Activity activity) {
        this.activity = activity;
        initMap();
        generateTiles();
        cars = new ArrayList<Car>();
        startPoint = new Point(100,100);
    }

    public Game(int width, int height) {

    }

    private void initMap() {
        mapManager = new MapManager(activity);
        //   tiles = mapManager.getRandomizeTiles(WIDTH, HEIGHT);
        //  tiles = mapManager.loadMapFromFile("mapHouses.json");
        // tiles = mapManager.loadMapFromFile("mapHouseDoor.json");
        //tiles = mapManager.loadMapFromFile("water_house_test.json");
        tiles = mapManager.loadMapFromFile("river.json");
    }
   @Deprecated
    private void generateTiles() {
    }



    public void update() {
        for (Car car : cars) {
            car.update();
        }
    }

    public int[][] getTiles() {
        return tiles;
    }

    public int getCellSize() {
        return cellSize;
    }


    public Car createNewCar() {
        Car car = new Car(startPoint.getXIntValue(), startPoint.getYIntValue(), this);
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

    public MapManager getMapManager() {
        return mapManager;
    }

    public void draw(SpriteBatch batch) {

    }
}
