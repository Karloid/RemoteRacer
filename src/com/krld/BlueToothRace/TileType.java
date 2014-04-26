package com.krld.BlueToothRace;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Created by Andrey on 2/19/14.
 */
public class TileType {

    private final int id;
    private final String name;
    private final String texture;
    private final Collection<String> tags;
    private final Bitmap bitmap;

    public Collection<String> getTags() {
        return tags;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTexture() {
        return texture;
    }

    public TileType(int id, String name, String texture, Collection<String> tags, Bitmap bitmap) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.tags = tags;
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "[id: " + id + ", name: " + name + ", texture: " + texture + " ]";
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
