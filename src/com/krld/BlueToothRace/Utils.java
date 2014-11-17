package com.krld.BlueToothRace;

import android.app.Activity;
import android.graphics.*;
import android.content.res.*;
import android.graphics.BitmapFactory.*;
import android.util.Log;
import com.krld.BlueToothRace.activitys.ServerActivity;
import com.krld.BlueToothRace.model.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static Bitmap loadSprite(int rawFileId, Resources resources, int scale, String textureName) {
        Options options = new
                BitmapFactory.Options();
        options.inScaled = false;
        Bitmap sprite;
        sprite = BitmapFactory.decodeResource(resources, rawFileId, options);
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
        return getDistance(point1.getXIntValue(), point1.getYIntValue(), point2.getXIntValue(), point2.getYIntValue());
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

    public static String readFile(String fileName) {
        String string = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                string += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return string;
    }
}
