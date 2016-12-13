package com.v5ent.game.battle;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.entities.Monster;

public class MonsterFactory {
    private static Json json = new Json();
    public static enum MonsterType{
        MONSTER001,MONSTER002,MONSTER003,MONSTER004,MONSTER005,
        MONSTER006,MONSTER007,MONSTER008,MONSTER009,MONSTER010,
        MONSTER011,MONSTER012,MONSTER013,MONSTER014,MONSTER015,
        MONSTER016,MONSTER017,MONSTER018,MONSTER019,MONSTER020,
        MONSTER021,MONSTER022,MONSTER023,MONSTER024,MONSTER025,
        MONSTER026,MONSTER027,MONSTER028,MONSTER029,MONSTER030,
        MONSTER031,MONSTER032,MONSTER033,MONSTER034,MONSTER035,
        MONSTER036,MONSTER037,MONSTER038,MONSTER039,MONSTER040,
        MONSTER041, MONSTER042,
        NONE
    }

    private static MonsterFactory instance = null;
    private Hashtable<String, Monster> entities;
    private Hashtable<String, Array<MonsterType>> monsterZones;

    private MonsterFactory(){
        entities = Monsters.initDatas();//json.fromJson(Hashtable.class, Gdx.files.internal("scripts/monsters.json"));
        monsterZones = MonsterZone.getMonsterZones("data/monster_zones.json");
    }

    public static MonsterFactory getInstance() {
        if (instance == null) {
            instance = new MonsterFactory();
        }
        return instance;
    }

    public Monster getMonster(MonsterType monsterMonsterType){
       return Monsters.initData(entities.get(monsterMonsterType.toString()));
    }

    public Monster getRandomMonster(int monsterZoneID){
        Array<MonsterType> monsters = monsterZones.get(String.valueOf(monsterZoneID));
        int size = monsters.size;
        if( size == 0 ){
            return null;
        }
        int randomIndex = MathUtils.random(size - 1);

        return getMonster(monsters.get(randomIndex));
    }

}
