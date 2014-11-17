package com.krld.BlueToothRace;

import android.app.Activity;
import android.graphics.Bitmap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.*;


/**
 * Created by Andrey on 2/18/14.
 */

// cmd command for rename
// for /f "Tokens=*" %f in ('dir /l/b/a-d') do (rename "%f" "%f")
public class MapManager {
    private static final int GDX_TEXTURE_SIZE = 32;
    private final Activity activity;
    private boolean inited = false;
    public static Map<Integer, TileType> tileTypes;
    private int mapWidth;
    private int mapHeight;

    public MapManager(Activity activity) {
        this.activity = activity;
    }

    private void init() {
        loadTiles();
        inited = true;
    }

    private void loadTiles() {
        tileTypes = new HashMap<Integer, TileType>();
        InputStream iFile = activity.getResources().openRawResource(R.raw.tiletypes);
        String jsonString = inputStreamToString(iFile);
        Map root = new Gson().fromJson(jsonString, Map.class);
        List<Map<String, Object>> tiles = (List<Map<String, Object>>) root.get("tiles");
        for (Map<String, Object> tile : tiles) {
            tileTypes.put((int) Math.round((Double) tile.get("id")), new TileType((int) Math.round((Double) tile.get("id")),
                    (String) tile.get("name"), (String) tile.get("texture"), (Collection<String>) tile.get("tags"),
                    getBitmapByName((String) tile.get("texture"))));
        }
    }

    private Bitmap getBitmapByName(String texture) {
        texture = texture.split("\\.")[0].toLowerCase();
        return Utils.loadSprite(activity.getResources().getIdentifier(texture, "raw", activity.getPackageName()), activity.getResources(), 2, texture);
    }

    private Bitmap getBitmapFromRawResourcesByName(String texture, Activity activity) {

        return null;
    }

    private String inputStreamToString(InputStream iFile) {
        Scanner scanner = new Scanner(iFile).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public TileType getTileTypeByName(String name) {
      /*  for (TileType tileType : tiletypes1) {
            if (tileType.getName().equals(name)) {
                return tileType;
            }
        }            */
        return null;
    }

    public boolean haveTag(int id, String tag) {
        TileType tileType = getTileTypeById(id);
        for (String str : tileType.getTags()) {
            if (str.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private TileType getTileTypeById(int id) {
       /* for (TileType tileType : tileTypes) {
            if (tileType.getId() == id) {
                return tileType;
            }
        }          */
        return null;
    }

    public int[][] loadMapFromFile(String fileName) {
        if (!inited) {
            init();
        }
        //  String jsonMapString = Utils.readFile("maps/" + fileName);
      //  InputStream iFile = activity.getResources().openRawResource(R.raw.river);
        InputStream iFile = activity.getResources().openRawResource(R.raw.bigracemap);
        String jsonMapString = inputStreamToString(iFile);
        Map root = new Gson().fromJson(jsonMapString, Map.class);
        //System.out.println("DEBUG INFO: " + root.get("tiles").getClass());
        mapWidth = (int) Math.round((Double) root.get("width"));
        mapHeight = (int) Math.round((Double) root.get("height"));
        ArrayList<ArrayList<Double>> tilesArrayList = (ArrayList<ArrayList<Double>>) root.get("tiles");
        int[][] tiles = new int[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                tiles[x][y] = (int) Math.round(tilesArrayList.get(x).get(y));
                System.out.print(" " + tiles[x][y]);
            }
            System.out.println();
        }
        return tiles;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    private TextureRegion loadTextureRegion(String fileName, int width, int height) {
        Texture texture = new Texture(Gdx.files.internal(fileName.toLowerCase()));
        return new TextureRegion(texture, 0, 0, width, height);
    }

    public void loadGdxTextures() {
        for (Map.Entry<Integer, TileType> entry : tileTypes.entrySet()) {
            TileType tileType = entry.getValue();
            tileType.setGdxTextureRegion(loadTextureRegion(tileType.getTexture(), GDX_TEXTURE_SIZE,GDX_TEXTURE_SIZE));
        }

    }
}
