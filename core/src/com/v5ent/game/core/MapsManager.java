package com.v5ent.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

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
    public static int rows;
    public static int cols;

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
        loadMap("yaka_port");
    }

    public void loadMap(String mapId) {
        AssetsManager.AssetTiledMap assetTiledMap = AssetsManager.instance.assetTiledMaps.get(mapId);
        this.mapName = assetTiledMap.mapName;
        this.map = assetTiledMap.tiledMap;
        this.cols = assetTiledMap.mapTileWidth;
        this.rows = assetTiledMap.mapTileHeight;
        Gdx.app.debug(TAG,"load map:"+mapName);

    }
}
