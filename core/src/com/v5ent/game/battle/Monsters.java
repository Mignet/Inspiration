package com.v5ent.game.battle;

import com.v5ent.game.entities.Monster;

import java.util.Hashtable;

/**
 * Created by Administrator on 2016/12/11.
 */

public class Monsters {
    static int hp =15;
    static int ap =40;
    static int dp =5;
    static int xp =5;
    static int gp =5;
    public static Monster  initData(Monster monster){
        monster.setHealthPoint(hp);
        monster.setAttackPoint(ap);
        monster.setDefensePoint(dp);
        monster.setXpReward(xp);
        monster.setGpReward(gp);
        return monster;
    }

    public static Hashtable<String, Monster> initDatas(){
        Hashtable<String, Monster> configs =  new Hashtable<String, Monster>();
        Monster monster1 = new Monster(MonsterFactory.MonsterType.MONSTER001.toString(),0,0);
        initData(monster1);
        configs.put(MonsterFactory.MonsterType.MONSTER001.toString(),monster1);
        Monster monster2 = new Monster(MonsterFactory.MonsterType.MONSTER002.toString(),0,1);
        initData(monster2);
        configs.put(MonsterFactory.MonsterType.MONSTER002.toString(),monster2);
        Monster monster3 = new Monster(MonsterFactory.MonsterType.MONSTER003.toString(),0,2);
        initData(monster3);
        configs.put(MonsterFactory.MonsterType.MONSTER003.toString(),monster3);
        Monster monster4 = new Monster(MonsterFactory.MonsterType.MONSTER004.toString(),0,3);
        initData(monster4);
        configs.put(MonsterFactory.MonsterType.MONSTER004.toString(),monster4);
        Monster monster5 = new Monster(MonsterFactory.MonsterType.MONSTER005.toString(),0,4);
        initData(monster5);
        configs.put(MonsterFactory.MonsterType.MONSTER005.toString(),monster5);
        return configs;
    }
}
