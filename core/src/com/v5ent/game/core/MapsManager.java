package com.v5ent.game.core;

import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Created by Mignet on 2016/11/10.
 */

public class MapsManager {
    private static final String TAG = MapsManager.class.getName();
    public static final String BLOCK_LAYER = "block";
    public static final String GROUD_LAYER = "groud";
    public static final String FLOOR_LAYER = "floor";
    public static final String CEILING_LAYER = "ceiling";
    public static final float CELL_UNIT = 32.0f;

    private String mapName;
    private TiledMap map;
    public MapsManager() {
    }
}
