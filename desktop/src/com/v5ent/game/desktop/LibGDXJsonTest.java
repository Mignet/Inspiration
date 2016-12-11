package com.v5ent.game.desktop;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.battle.MonsterFactory;
import com.v5ent.game.entities.Monster;
import com.v5ent.game.inventory.InventoryItem;
import com.v5ent.game.utils.Assets;

import org.lwjgl.Sys;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Mignet on 2016/12/6.
 */

public class LibGDXJsonTest {
    private static Json json = new Json();
    //initInventory
    public static void main(String[] args){
        Array<InventoryItem.ItemTypeID> inventory = new Array<InventoryItem.ItemTypeID>();
        inventory.add(InventoryItem.ItemTypeID.ARMOR04);
        inventory.add(InventoryItem.ItemTypeID.BOOTS03);
        inventory.add(InventoryItem.ItemTypeID.HELMET05);
        inventory.add(InventoryItem.ItemTypeID.POTIONS01);
        inventory.add(InventoryItem.ItemTypeID.POTIONS01);
        inventory.add(InventoryItem.ItemTypeID.POTIONS01);
        inventory.add(InventoryItem.ItemTypeID.POTIONS01);
        inventory.add(InventoryItem.ItemTypeID.SCROLL01);
        inventory.add(InventoryItem.ItemTypeID.SCROLL01);
        inventory.add(InventoryItem.ItemTypeID.SCROLL01);
        inventory.add(InventoryItem.ItemTypeID.SHIELD02);
        inventory.add(InventoryItem.ItemTypeID.WANDS02);
        inventory.add(InventoryItem.ItemTypeID.WEAPON01);
        String jsonStr = json.toJson(inventory);
        System.out.println(jsonStr);
        Array<InventoryItem.ItemTypeID> n = json.fromJson(Array.class,jsonStr);
        for(int i=0;i<n.size;i++){
            System.out.println(n.get(i));
        }
        /*Hashtable<String, Monster> configs =  new Hashtable<String, Monster>();
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
        String jsonStr = json.toJson(configs);
        System.out.println(jsonStr);
        Hashtable<String, Monster> map = json.fromJson(Hashtable.class,jsonStr);
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key+":"+val);
        }*/
    }
}
