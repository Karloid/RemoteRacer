package com.krld.BlueToothRace.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.krld.BlueToothRace.MapManager;
import com.krld.BlueToothRace.model.Car;
import com.krld.BlueToothRace.model.Game;
import com.krld.BlueToothRace.model.Point;

/**
 * Created by Andrey on 5/25/2014.
 */
public class GdxWorldRenderer {
    private static final float CAR_SIZE = 32;
    private Game game;
    private int viewWidth;
    private int viewHeight;
    private TextureRegion redCarSprite;

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void init() {
        game.getMapManager().loadGdxTextures();
        viewWidth = 1280;
        viewHeight = 720;
        Texture texture = new Texture(Gdx.files.internal("carred.png"));
        redCarSprite = new TextureRegion(texture, 0, 0, 128, 128);
    }

    public void drawGame(SpriteBatch batch) {
        Car mainCar = game.getMainCar();
        Point cameraCenterPos;
        if (mainCar != null) {
            cameraCenterPos = mainCar.pos;
        } else {
            cameraCenterPos = new Point(0, 0);
        }
        drawTiles(game, batch, cameraCenterPos);

        for (Car car : game.getCars()) {
            drawCar(car, batch, cameraCenterPos);
        }

    }

    public void drawCar(Car car, SpriteBatch batch, Point cameraCenterPos) {
/*        Utils.drawBitmapRotate(redCarSprite,
                car.pos.getXIntValue() - cameraCenterPos.getXIntValue() + viewWidth / 2 - 32 / 2,
                car.pos.getYIntValue() - cameraCenterPos.getYIntValue() + viewHeight / 2 - 32/ 2, car.getAngle() + 90);*/
        batch.draw(redCarSprite, car.pos.getXIntValue() - cameraCenterPos.getXIntValue() + viewWidth / 2 - CAR_SIZE / 2,
                car.pos.getYIntValue() - cameraCenterPos.getYIntValue() + viewHeight / 2 - CAR_SIZE / 2,
                CAR_SIZE / 2, CAR_SIZE / 2
                , CAR_SIZE, CAR_SIZE, 1, 1, car.getAngle() - 90);
    }

    private void drawTiles(Game game, SpriteBatch batch, Point pos) {
        int scaledX;
        int scaledY;
        for (int x = 0; x < game.getMapManager().getMapWidth(); x++) {
            for (int y = 0; y < game.getMapManager().getMapHeight(); y++) {
                scaledX = x * game.getCellSize() - game.getCellSize() / 2 - pos.getXIntValue() + viewWidth / 2;
                if (scaledX > viewWidth || scaledX < -64) {
                    continue;
                }
                scaledY = game.getMapManager().getMapHeight() - y * game.getCellSize() - game.getCellSize() / 2 - pos.getYIntValue() + viewHeight / 2;
                if (scaledY > viewHeight || scaledY < -64) {
                    continue;
                }
                batch.draw(MapManager.tileTypes.get(game.getTiles()[x][y]).getGdxTextureRegion(), scaledX, scaledY);
            }
        }
    }
}
