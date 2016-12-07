package com.v5ent.game.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Resources file-path map
 * Created by Mignet on 2016/11/13.
 */

public class Resource {
    public static final String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.pack";
    public static final String ITEMS_TEXTURE_ATLAS_PATH = "items/items.pack";
    public static final String STATUSUI_SKIN_PATH = "skins/statusui.json";
    //single
    public static final String TOUCH = "tools/touch.png";
    public static final String SHADOW = "tools/shadow.png";
    public static final String SELECTED = "tools/selected.png";
    public Map<String,String> players = new HashMap<String,String>();
    public Map<String,String> npcs = new HashMap<String,String>();
    public Map<String,String> maps = new HashMap<String,String>();
    public static final Resource instance = new Resource();
    private Resource(){
        players.put("lante","heros/lante.png");
        players.put("hafei","heros/hafei.png");
        npcs.put("knight","heros/knight.png");
        npcs.put("NPC-01","heros/NPC-01.png");
        npcs.put("ASL","heros/ASL.png");
        maps.put("yaka_port","maps/yaka_port.tmx");
        maps.put("home_one","maps/home_one.tmx");
    }
}
