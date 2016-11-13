package com.v5ent.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;

import java.util.ArrayList;
import java.util.List;

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
    public List<Npc> npcs = new ArrayList<Npc>();
    /**START POINT*/
    public static final Vector2 START_POINT = new Vector2(14,15);

    public TiledMap getCurrentMap() {
        return map;
    }

    public TiledMapTileLayer getBlockLayer(){
        return (TiledMapTileLayer)this.map.getLayers().get(BLOCK_LAYER);
    }

    public MapsManager() {
        loadMap("yaka_port");
    }

    public void loadMap(String mapId) {
        com.v5ent.game.utils.AssetsManager.AssetTiledMap assetTiledMap = com.v5ent.game.utils.AssetsManager.instance.assetTiledMaps.get(mapId);
        this.mapName = assetTiledMap.mapName;
        this.map = assetTiledMap.tiledMap;
        this.cols = assetTiledMap.mapTileWidth;
        this.rows = assetTiledMap.mapTileHeight;
        MapLayer npcLayer = this.map.getLayers().get("npc");
        if(npcLayer!=null){
            for(MapObject o:npcLayer.getObjects()){
                if(o instanceof RectangleMapObject) {
                    RectangleMapObject r = (RectangleMapObject)o;
                    Npc n = new Npc(r.getName());
                    if (n != null) {
                        Gdx.app.debug(TAG,"NPC:"+r.getRectangle().x/32+","+r.getRectangle().y/32+"|"+r.getRectangle());
                        n.setPosInMap(new Vector2(r.getRectangle().x/32,r.getRectangle().y/32));
                        if("DOWN".equals(r.getProperties().get("Dir"))){
                            n.setCurrentDir(Role.Direction.DOWN);
                        }
                        if("UP".equals(r.getProperties().get("Dir"))){
                            n.setCurrentDir(Role.Direction.UP);
                        }
                        if("LEFT".equals(r.getProperties().get("Dir"))){
                            n.setCurrentDir(Role.Direction.LEFT);
                        }
                        if("RIGHT".equals(r.getProperties().get("Dir"))){
                            n.setCurrentDir(Role.Direction.RIGHT);
                        }
                        this.npcs.add(n);
                    }
                }
            }
        }
        Gdx.app.debug(TAG,"load map:"+mapName);

    }
}
