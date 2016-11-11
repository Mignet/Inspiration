package com.v5ent.game.core;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.utils.Constants;

/**
 * Created by Mignet on 2016/11/10.
 */

public class MapsManager {
    private static final String TAG = MapsManager.class.getName();
    public static final String BLOCK_LAYER = "block";
    public static final String GROUND_LAYER = "ground";
    public static final String FLOOR_LAYER = "floor";
    public static final String CEILING_LAYER = "ceiling";
    public static final float CELL_UNIT = 32.0f;

    private String mapName;
    private TiledMap map;
    /**START POINT*/
    public static final Vector2 START_POINT = new Vector2(14,15);

    public TiledMap getCurrentMap() {
        return map;
    }

   /* public MapLayer getPortalLayer(){
        return this.map.getLayers().get(BLOCK_LAYER);
    }*/
    public TiledMapTileLayer getBlockLayer(){
        return (TiledMapTileLayer)this.map.getLayers().get(BLOCK_LAYER);
    }

    public MapsManager() {
        loadMap("001");
    }

    public void loadMap(String mapId) {
        AssetsManager.AssetTiledMap assetTiledMap = AssetsManager.instance.assetTiledMaps.get(mapId);
        this.mapName = assetTiledMap.mapName;
        this.map = assetTiledMap.tiledMap;
    }
}
