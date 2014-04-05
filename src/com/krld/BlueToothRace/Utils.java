package com.krld.BlueToothRace;

import android.graphics.*;
import android.content.res.*;
import android.graphics.BitmapFactory.*;
import com.krld.BlueToothRace.model.Point;

public class Utils {

    public static Bitmap loadSprite(int rawFile, Resources resources, int scale) {
        Options options = new
                BitmapFactory.Options();
        options.inScaled = false;
        Bitmap sprite;
        sprite = BitmapFactory.decodeResource(resources, rawFile, options);
        sprite = Bitmap.createScaledBitmap(sprite, sprite.getWidth() * scale, sprite.getHeight() * scale, false);
        return sprite;
    }

    public static void drawBitmapRotate(Bitmap sprite, float x, float y, float angle, Canvas canvas, Paint paint) {
        Matrix rotator = new Matrix();
        rotator.postRotate(angle, sprite.getWidth() / 2, sprite.getHeight() / 2);
        //	rotator.postTranslate(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        rotator.postTranslate(x, y);
        canvas.drawBitmap(sprite, rotator, paint);

    }

    public static float getAngle(float x, float y) {
        if (x == 0) return (y > 0) ? 180 : 0;
        float a = (float) (Math.atan(y / x) * 180 / Math.PI);
        a = (x > 0) ? a + 90 : a + 270;
        return a;

    }

    public static double getDistance(com.krld.BlueToothRace.model.Point point1, Point point2) {
        return getDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    public static double getDistance(float x1, float y1, float x2, float y2) {
        double result;
        result = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        result = Math.sqrt(result);
        return result;
    }

    public static String getExceptionContent(Exception e) {
        String result = e.getMessage();
        for (StackTraceElement element : e.getStackTrace()) {
            result += "\n " + element.toString();
        }
        return result;
    }

    public static boolean userIsAMonkey() {
        return true;
    }
}
