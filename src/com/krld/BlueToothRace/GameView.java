package com.krld.BlueToothRace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by 3 on 28.01.14.
 */
public class GameView extends View {
    private final Game game;
    private Paint paint;

    public GameView(Context context, Game game) {
        super(context);
        this.game = game;
        WorldRenderer.init(getResources());
        setMinimumHeight(300);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null)
            paint = new Paint();
        drawCar(canvas, paint);
    }

    private void drawCar(Canvas canvas, Paint paint) {
        WorldRenderer.drawGame(game, canvas, paint);

    }
}
