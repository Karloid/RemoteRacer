package com.krld.BlueToothRace.gdx;

import com.krld.BlueToothRace.model.Game;

/**
 * Created by Andrey on 5/25/2014.
 */
public class GdxGameManager {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public static GdxGameView getNewGameView() {
        GdxGameView gdxGameView = new GdxGameView();
    //    Game game = new Game(WIDTH, HEIGHT);
     //   gdxGameView.setGame(game);
        return gdxGameView;
    }
}
