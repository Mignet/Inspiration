package com.v5ent.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.entities.Trap;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.utils.Assets;

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
    public final static String ENEMY_LAYER = "enemy";
    protected MapLayer enemySpawnLayer = null;
    public static final float CELL_UNIT = 32.0f;
    public int rows;
    public int cols;
    public int width;
    public int height;

    public String getMapName() {
        return mapName;
    }

    private String mapName;
    private TiledMap map;
    public List<Npc> npcs ;
    public List<Trap> traps;

    /**START POINT*/
    public static final Vector2 START_POINT = new Vector2(14,15);
    public static final String START_MAP = "yaka_port";

    public TiledMap getCurrentMap() {
        return map;
    }

    public TiledMapTileLayer getBlockLayer(){
        return (TiledMapTileLayer)this.map.getLayers().get(BLOCK_LAYER);
    }

    public MapsManager(Role player) {
        loadMap(START_MAP);
        player.setPosInMap(START_POINT);
    }

    public void loadMap(String mapId) {
        npcs = new ArrayList<Npc>();
        traps = new ArrayList<Trap>();
        Assets.AssetTiledMap assetTiledMap = Assets.instance.assetTiledMaps.get(mapId);
        this.mapName = assetTiledMap.mapName;
        this.map = assetTiledMap.tiledMap;
        this.cols = assetTiledMap.mapTileWidth;
        this.rows = assetTiledMap.mapTileHeight;

        this.width = assetTiledMap.mapPixelWidth;
        this.height = assetTiledMap.mapPixelHeight;
        MapLayer npcLayer = this.map.getLayers().get("npc");
        if(npcLayer!=null){
            for(MapObject o:npcLayer.getObjects()){
                if(o instanceof RectangleMapObject) {
                    RectangleMapObject r = (RectangleMapObject)o;
                    Npc n = new Npc(r.getName());
                    if (n != null) {
                        Gdx.app.debug(TAG,"NPC:"+r.getRectangle().x/32+","+r.getRectangle().y/32+"|"+r.getRectangle()+"|"+r.getProperties().get("Dir")+"|"+r.getProperties().get("State"));
                        n.setPosInMap(new Vector2(r.getRectangle().x/32,r.getRectangle().y/32));
                        n.setCurrentDir(Role.Direction.valueOf(r.getProperties().get("Dir",String.class)));
                        //npc needs a default
                        n.setDefaultDir(Role.Direction.valueOf(r.getProperties().get("Dir",String.class)));
                        //Npc state
                        if("FIXED".equals(r.getProperties().get("State",String.class))){
                            n.setState(Role.State.FIXED);
                            n.setDefaultState(Role.State.FIXED);
                        }else{
                            n.setState(Role.State.IDLE);
                            n.setDefaultState(Role.State.IDLE);
                        }
                        this.npcs.add(n);
                    }
                }
            }
        }
        MapLayer eventLayer = this.map.getLayers().get("event");
        if(eventLayer!=null) {
            for (MapObject o : eventLayer.getObjects()) {
                if(o instanceof TextureMapObject){
                    TextureMapObject event = (TextureMapObject)o;
                    Trap eo = new Trap(event.getName(),event.getTextureRegion(),event.getX(),event.getY());
                    eo.setCommand(event.getProperties().get("CMD",String.class));
                    this.traps.add(eo);
                    Gdx.app.debug(TAG,"load map event:"+eo.getName()+"|"+eo.getCommand());
                }
            }
        }
        enemySpawnLayer = this.map.getLayers().get(ENEMY_LAYER);
        if( enemySpawnLayer == null ){
            Gdx.app.debug(TAG, "No enemy layer found!");
        }
        Gdx.app.debug(TAG,"load map:"+mapName);

    }
    public MapLayer getEnemySpawnLayer() {
        return enemySpawnLayer;
    }
}
