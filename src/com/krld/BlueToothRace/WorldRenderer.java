package com.krld.BlueToothRace;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;

/**
 * Created by 3 on 28.01.14.
 */
public class WorldRenderer {

    private static Bitmap carSprite;
    private static HashMap<TileType, Bitmap> tileSprites;
    private static int viewWidth;
    private static int viewHeight;

    public static void drawGame(Game game, Canvas canvas, Paint paint) {
        Point pos = game.getCar().pos;
        drawTiles(game, canvas, paint, pos);
        drawCar(game.getCar(), canvas, paint, pos);
    }

    public static void drawCar(Car car, Canvas canvas, Paint paint, Point pos) {
        Utils.drawBitmapRotate(carSprite,
                car.pos.getX() - pos.getX() + viewWidth / 2 - carSprite.getWidth() / 2, car.pos.getY() - pos.getY() + viewHeight / 2 - carSprite.getHeight() / 2, car.getAngle() + 90, canvas, paint);
      //  canvas.drawCircle(car.pos.getX() - pos.getX() + viewWidth / 2, car.pos.getY() - pos.getY() + viewHeight / 2, 2, paint);
    }

    private static void drawTiles(Game game, Canvas canvas, Paint paint, Point pos) {
        int scaledX;
        int scaledY;
        for (int x = 0; x < game.FIELD_SIZE; x++) {
            for (int y = 0; y < game.FIELD_SIZE; y++) {
                scaledX = x * game.getCellSize() - game.getCellSize() / 2  - pos.getX() + viewWidth / 2;
                if (scaledX > viewWidth || scaledX < -64) {
                    continue;
                }
                scaledY = y * game.getCellSize() - game.getCellSize() / 2  - pos.getY() + viewHeight / 2;
                if (scaledY > viewHeight || scaledY < -64) {
                    continue;
                }
                canvas.drawBitmap(tileSprites.get(game.getTiles()[x][y]), scaledX , scaledY , paint);
            }
        }
    }

    public static void init(Resources resources) {
        viewWidth = ServerActivity.VIEW_WIDTH;
        viewHeight = ServerActivity.VIEW_HEIGHT;
        carSprite = Utils.loadSprite(R.raw.carbig, resources, 1);
        tileSprites = new HashMap<TileType, Bitmap>();
        tileSprites.put(TileType.GRASS1, Utils.loadSprite(R.raw.grass1, resources, 64 / 8));
        tileSprites.put(TileType.GRASS2, Utils.loadSprite(R.raw.grass2, resources, 64 / 8));
        tileSprites.put(TileType.GRASS3, Utils.loadSprite(R.raw.grass3, resources, 64 / 8));
        tileSprites.put(TileType.GRASS4, Utils.loadSprite(R.raw.grass4, resources, 64 / 8));
        tileSprites.put(TileType.DIRT1, Utils.loadSprite(R.raw.dirt1, resources, 64 / 8));
    }
}
