package com.v5ent.game.battle;

import com.v5ent.game.entities.Monster;

import java.util.Hashtable;

/**
 * Created by Administrator on 2016/12/11.
 */

public class Monsters {
    public static Hashtable<String, Monster> initDatas(){
        Hashtable<String, Monster> configs =  new Hashtable<String, Monster>();
        Monster monster1 = new Monster(MonsterFactory.MonsterType.MONSTER001.toString(),0,0);
        monster1.setHealthPoint(15);
        monster1.setAttackPoint(40);
        monster1.setDefensePoint(5);
        monster1.setXpReward(5);
        monster1.setGpReward(5);
        configs.put(MonsterFactory.MonsterType.MONSTER001.toString(),monster1);
        Monster monster2 = new Monster(MonsterFactory.MonsterType.MONSTER002.toString(),0,1);
        monster2.setHealthPoint(15);
        monster2.setAttackPoint(40);
        monster2.setDefensePoint(5);
        monster2.setXpReward(5);
        monster2.setGpReward(5);
        configs.put(MonsterFactory.MonsterType.MONSTER002.toString(),monster2);
        Monster monster3 = new Monster(MonsterFactory.MonsterType.MONSTER003.toString(),0,2);
        monster3.setHealthPoint(15);
        monster3.setAttackPoint(40);
        monster3.setDefensePoint(5);
        monster3.setXpReward(5);
        monster3.setGpReward(5);
        configs.put(MonsterFactory.MonsterType.MONSTER003.toString(),monster3);
        Monster monster4 = new Monster(MonsterFactory.MonsterType.MONSTER004.toString(),0,3);
        monster4.setHealthPoint(15);
        monster4.setAttackPoint(40);
        monster4.setDefensePoint(5);
        monster4.setXpReward(5);
        monster4.setGpReward(5);
        configs.put(MonsterFactory.MonsterType.MONSTER004.toString(),monster4);
        Monster monster5 = new Monster(MonsterFactory.MonsterType.MONSTER005.toString(),0,4);
        monster5.setHealthPoint(15);
        monster5.setAttackPoint(40);
        monster5.setDefensePoint(5);
        monster5.setXpReward(5);
        monster5.setGpReward(5);
        configs.put(MonsterFactory.MonsterType.MONSTER005.toString(),monster5);
        return configs;
    }
}
