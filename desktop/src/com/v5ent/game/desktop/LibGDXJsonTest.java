package com.v5ent.game.desktop;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.inventory.InventoryItem;

/**
 * Created by Mignet on 2016/12/6.
 */

public class LibGDXJsonTest {
    private static Json json = new Json();
    //initInventory
    public static void main(String[] args){
        Array<InventoryItem.ItemTypeID> inventory = new Array<InventoryItem.ItemTypeID>();;
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
    }
}
