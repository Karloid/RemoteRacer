package com.krld.BlueToothRace;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.krld.BlueToothRace.model.Point;
import com.krld.BlueToothRace.model.Car;
import com.krld.BlueToothRace.model.Game;

import java.util.HashMap;

/**
 * Created by 3 on 28.01.14.
 */
public class WorldRenderer {

    private static Bitmap blueCarSprite;
    private static Bitmap redCarSprite;
    private static HashMap<TileType, Bitmap> tileSprites;
    private static int viewWidth;
    private static int viewHeight;

    public static void drawGame(Game game, Canvas canvas, Paint paint) {
        Car mainCar = game.getMainCar();
        Point cameraCenterPos;
        if (mainCar != null) {
            cameraCenterPos = mainCar.pos;

        } else {
            cameraCenterPos = new Point(0, 0);
        }
        drawTiles(game, canvas, paint, cameraCenterPos);
        for (Car car : game.getCars()) {
            drawCar(car, canvas, paint, cameraCenterPos, redCarSprite);
        }
    }

    private static void drawLineBetweenCars(Car car1, Car car2, Point cameraCenterPos, Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawLine(car1.pos.getXIntValue() - cameraCenterPos.getXIntValue() + viewWidth / 2,
                car1.pos.getYIntValue() - cameraCenterPos.getYIntValue() + viewHeight / 2,
                car2.pos.getXIntValue() - cameraCenterPos.getXIntValue() + viewWidth / 2,
                car2.pos.getYIntValue() - cameraCenterPos.getYIntValue() + viewHeight / 2, paint);

    }

    public static void drawCar(Car car, Canvas canvas, Paint paint, Point cameraCenterPos, Bitmap carSprite) {
        Utils.drawBitmapRotate(carSprite,
                car.pos.getXIntValue() - cameraCenterPos.getXIntValue() + viewWidth / 2 - blueCarSprite.getWidth() / 2,
                car.pos.getYIntValue() - cameraCenterPos.getYIntValue() + viewHeight / 2 - blueCarSprite.getHeight() / 2, car.getAngle() + 90, canvas, paint);
    }

    private static void drawTiles(Game game, Canvas canvas, Paint paint, Point pos) {
        int scaledX;
        int scaledY;
        for (int x = 0; x < game.getMapManager().getMapWidth(); x++) {
            for (int y = 0; y < game.getMapManager().getMapHeight(); y++) {
                scaledX = x * game.getCellSize() - game.getCellSize() / 2 - pos.getXIntValue() + viewWidth / 2;
                if (scaledX > viewWidth || scaledX < -64) {
                    continue;
                }
                scaledY = y * game.getCellSize() - game.getCellSize() / 2 - pos.getYIntValue() + viewHeight / 2;
                if (scaledY > viewHeight || scaledY < -64) {
                    continue;
                }
                canvas.drawBitmap(MapManager.tileTypes.get(game.getTiles()[x][y]).getBitmap(), scaledX, scaledY, paint);
            }
        }
    }

    public static void init(Resources resources) {
        viewWidth = Constants.VIEW_WIDTH;
        viewHeight = Constants.VIEW_HEIGHT;
        blueCarSprite = Utils.loadSprite(R.raw.carblue, resources, 1, "carblue");
        redCarSprite = Utils.loadSprite(R.raw.carred, resources, 1, "carRed");
    }
}
