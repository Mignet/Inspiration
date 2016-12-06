package com.v5ent.game.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.v5ent.game.hud.InventoryUI;
import com.v5ent.game.hud.StoreInventoryUI;
import com.v5ent.game.utils.Assets;

public class InventorySlot extends Stack {

    //All slots have this default image
    private Stack _defaultBackground;
    private Image _customBackgroundDecal;
    private Label numItemsLabel;
    private int numItemsVal = 0;
    private int _filterItemType;

    InventoryUI inventoryUI = null;
    StoreInventoryUI storeInventoryUI = null;

    //empty slot
    public InventorySlot(){
        _filterItemType = 0; //filter nothing
        _defaultBackground = new Stack();
        _customBackgroundDecal = new Image();
        Image image = new Image(Assets.instance.STATUSUI_SKIN,"cell");

        numItemsLabel = new Label(String.valueOf(numItemsVal), Assets.instance.STATUSUI_SKIN);
        numItemsLabel.setAlignment(Align.bottomRight);
        numItemsLabel.setVisible(false);

        _defaultBackground.add(image);

        _defaultBackground.setName("background");
        numItemsLabel.setName("numitems");

        this.add(_defaultBackground);
        this.add(numItemsLabel);
    }

    public InventorySlot(InventoryUI inventoryUI,int filterItemType, Image customBackgroundDecal){
        this();
        this.inventoryUI =  inventoryUI;
        _filterItemType = filterItemType;
        _customBackgroundDecal = customBackgroundDecal;
        _defaultBackground.add(_customBackgroundDecal);
    }
    public InventorySlot(StoreInventoryUI storeInventoryUI){
        this();
        this.storeInventoryUI =  storeInventoryUI;
    }

    public void decrementItemCount(boolean sendRemoveNotification) {
        numItemsVal--;
        numItemsLabel.setText(String.valueOf(numItemsVal));
        if( _defaultBackground.getChildren().size == 1 ){
            _defaultBackground.add(_customBackgroundDecal);
        }
        checkVisibilityOfItemCount();
        if( sendRemoveNotification ){
//            notify(this, InventorySlotObserver.SlotEvent.REMOVED_ITEM);
            if(inventoryUI!=null){
                inventoryUI.removedItem(this);
            }
            if(storeInventoryUI!=null){
                storeInventoryUI.removedItem(this);
            }
        }
    }

    public void incrementItemCount(boolean sendAddNotification) {
        numItemsVal++;
        numItemsLabel.setText(String.valueOf(numItemsVal));
        if( _defaultBackground.getChildren().size > 1 ){
            _defaultBackground.getChildren().pop();
        }
        checkVisibilityOfItemCount();
        if( sendAddNotification){
//            notify(this, InventorySlotObserver.SlotEvent.ADDED_ITEM);
            if(inventoryUI!=null) {
                inventoryUI.addedItem(this);
            }
            if(storeInventoryUI!=null) {
                storeInventoryUI.addedItem(this);
            }
        }
    }

    @Override
    public void add(Actor actor) {
        super.add(actor);

        if( numItemsLabel == null ){
            return;
        }

        if( !actor.equals(_defaultBackground) && !actor.equals(numItemsLabel) ) {
            incrementItemCount(true);
        }
    }

    public void remove(Actor actor) {
        super.removeActor(actor);

        if( numItemsLabel == null ){
            return;
        }

        if( !actor.equals(_defaultBackground) && !actor.equals(numItemsLabel) ) {
            decrementItemCount(true);
        }
    }

    public void add(Array<Actor> array) {
        for( Actor actor : array){
            super.add(actor);

            if( numItemsLabel == null ){
                return;
            }

            if( !actor.equals(_defaultBackground) && !actor.equals(numItemsLabel) ) {
                incrementItemCount(true);
            }
        }
    }

    public Array<Actor> getAllInventoryItems() {
        Array<Actor> items = new Array<Actor>();
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  arrayChildren.size - 2;
            for(int i = 0; i < numInventoryItems; i++) {
                decrementItemCount(true);
                items.add(arrayChildren.pop());
            }
        }
        return items;
    }

    public void updateAllInventoryItemNames(String name){
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            //skip first two elements
            for(int i = arrayChildren.size - 1; i > 1 ; i--) {
                arrayChildren.get(i).setName(name);
            }
        }
    }

    public void removeAllInventoryItemsWithName(String name){
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            //skip first two elements
            for(int i = arrayChildren.size - 1; i > 1 ; i--) {
                String itemName = arrayChildren.get(i).getName();
                if( itemName.equalsIgnoreCase(name)){
                    decrementItemCount(true);
                    arrayChildren.removeIndex(i);
                }
            }
        }
    }


    public void clearAllInventoryItems(boolean sendRemoveNotifications) {
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  getNumItems();
            for(int i = 0; i < numInventoryItems; i++) {
                decrementItemCount(sendRemoveNotifications);
                arrayChildren.pop();
            }
        }
    }

    private void checkVisibilityOfItemCount(){
        if( numItemsVal < 2){
            numItemsLabel.setVisible(false);
        }else{
            numItemsLabel.setVisible(true);
        }
    }

    public boolean hasItem(){
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            if( items.size > 2 ){
                return true;
            }
        }
        return false;
    }

    public int getNumItems(){
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            return items.size - 2;
        }
        return 0;
    }

    public int getNumItems(String name){
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            int totalFilteredSize = 0;
            for( Actor actor: items ){
                if( actor.getName().equalsIgnoreCase(name)){
                    totalFilteredSize++;
                }
            }
            return totalFilteredSize;
        }
        return 0;
    }

    public boolean doesAcceptItemUseType(int itemUseType){
        if( _filterItemType == 0 ){
            return true;
        }else {
            return ((_filterItemType & itemUseType) == itemUseType);
        }
    }

    public InventoryItem getTopInventoryItem(){
        InventoryItem actor = null;
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            if( items.size > 2 ){
                actor = (InventoryItem) items.peek();
            }
        }
        return actor;
    }

    static public void swapSlots(InventorySlot inventorySlotSource, InventorySlot inventorySlotTarget, InventoryItem dragActor){
        //check if items can accept each other, otherwise, no swap
        if( !inventorySlotTarget.doesAcceptItemUseType(dragActor.getItemUseType()) ||
                !inventorySlotSource.doesAcceptItemUseType(inventorySlotTarget.getTopInventoryItem().getItemUseType())) {
            inventorySlotSource.add(dragActor);
            return;
        }

        //swap
        Array<Actor> tempArray = inventorySlotSource.getAllInventoryItems();
        tempArray.add(dragActor);
        inventorySlotSource.add(inventorySlotTarget.getAllInventoryItems());
        inventorySlotTarget.add(tempArray);
    }

}
