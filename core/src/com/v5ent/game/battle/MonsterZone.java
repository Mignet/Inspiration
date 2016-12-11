package com.v5ent.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Hashtable;

public class MonsterZone {
    private String zoneID;
    private Array<MonsterFactory.MonsterType> monsters;

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public Array<MonsterFactory.MonsterType> getMonsters() {
        return monsters;
    }

    public void setMonsters(Array<MonsterFactory.MonsterType> monsters) {
        this.monsters = monsters;
    }

    static public Hashtable<String, Array<MonsterFactory.MonsterType>> getMonsterZones(String configFilePath){
        Json json = new Json();
        Hashtable<String, Array<MonsterFactory.MonsterType>> monsterZones = new Hashtable<String, Array<MonsterFactory.MonsterType>>();

        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));

        for (JsonValue jsonVal : list) {
            MonsterZone zone = json.readValue(MonsterZone.class, jsonVal);
            monsterZones.put(zone.getZoneID(), zone.getMonsters());
        }

        return monsterZones;
    }
}
