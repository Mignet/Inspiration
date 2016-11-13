package com.v5ent.game.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mignet on 2016/11/13.
 */

public class Resource {
    public Map<String,String> players = new HashMap<String,String>();
    public Map<String,String> npcs = new HashMap<String,String>();
    public Map<String,String> maps = new HashMap<String,String>();
    public static final Resource instance = new Resource();
    private Resource(){
        players.put("lante","heros/lante.png");
        players.put("hafei","heros/hafei.png");
        npcs.put("knight","heros/knight.png");
        npcs.put("NPC-01","heros/NPC-01.png");
        maps.put("yaka_port","maps/yaka_port.tmx");
    }
}
