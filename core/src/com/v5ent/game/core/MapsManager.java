package com.v5ent.game.core;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.utils.AssetsManager;

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

    public static final Vector2 StartPoint = new Vector2(14,15);

    public TiledMap getCurrentMap() {
        return map;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public MapLayer getPortalLayer(){
        return this.map.getLayers().get(BLOCK_LAYER);
    }

    public MapsManager() {

    }

    public void loadMap(String mapName) {
        AssetsManager.instance.assetTiledMaps.get(mapName);
    }
}
