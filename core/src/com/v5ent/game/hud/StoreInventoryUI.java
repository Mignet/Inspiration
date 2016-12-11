package com.v5ent.game.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.inventory.InventoryItemLocation;
import com.v5ent.game.inventory.InventorySlot;
import com.v5ent.game.inventory.InventorySlotTarget;
import com.v5ent.game.inventory.InventorySlotTooltip;
import com.v5ent.game.inventory.InventorySlotTooltipListener;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.utils.Assets;

public class StoreInventoryUI extends Window{
    /** parent */
    private HUDScreen hudScreen;
    private int numStoreInventorySlots = 36;
    private int lengthSlotRow = 12;
    private Table inventorySlotTable;
    private Table playerInventorySlotTable;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private final int slotWidth = 46;
    private final int slotHeight = 46;

    private InventorySlotTooltip inventorySlotTooltip;

    private Label sellTotalLabel;
    private Label buyTotalLabel;
    private Label playerTotalGP;

    private int tradeInVal = 0;
    private int fullValue = 0;
    private int playerTotal = 0;

    private Button sellButton;
    private Button buyButton;
    public TextButton closeButton;

    private Table buttons;
    private Table totalLabels;

    private Json json;

    private static String SELL = "卖出";
    private static String BUY = "买进";
    private static String GP = " 金币";
    private static String PLAYER_TOTAL = "玩家合计";

    public StoreInventoryUI(HUDScreen parent){
        super("店铺存货", Assets.instance.STATUSUI_SKIN, "solidbackground");
        this.hudScreen = parent;
        json = new Json();

        this.setFillParent(true);

        //create
        dragAndDrop = new DragAndDrop();
        inventoryActors = new Array<Actor>();
        inventorySlotTable = new Table();
        inventorySlotTable.setName(InventoryUI.STORE_INVENTORY);

        playerInventorySlotTable = new Table();
        playerInventorySlotTable.setName(InventoryUI.PLAYER_INVENTORY);
        inventorySlotTooltip = new InventorySlotTooltip(Assets.instance.STATUSUI_SKIN);

        sellButton = new TextButton(SELL, Assets.instance.STATUSUI_SKIN, "inventory");
        disableButton (sellButton, true);

        sellTotalLabel = new Label(SELL + " : " + tradeInVal + GP, Assets.instance.STATUSUI_SKIN);
        sellTotalLabel.setAlignment(Align.center);
        buyTotalLabel = new Label(BUY + " : " + fullValue + GP, Assets.instance.STATUSUI_SKIN);
        buyTotalLabel.setAlignment(Align.center);

        playerTotalGP = new Label(PLAYER_TOTAL + " : " + playerTotal +  GP, Assets.instance.STATUSUI_SKIN);

        buyButton = new TextButton(BUY, Assets.instance.STATUSUI_SKIN, "inventory");
        disableButton (buyButton, true);

        closeButton = new TextButton("X", Assets.instance.STATUSUI_SKIN);

        buttons = new Table();
        buttons.defaults().expand().fill();
        buttons.add (sellButton).padLeft(10).padRight(10);
        buttons.add (buyButton).padLeft(10).padRight(10);

        totalLabels = new Table();
        totalLabels.defaults().expand().fill();
        totalLabels.add (sellTotalLabel).padLeft(40);
        totalLabels.add();
        totalLabels.add (buyTotalLabel).padRight(40);

        //layout
        for(int i = 1; i <= numStoreInventorySlots; i++){
            InventorySlot inventorySlot = new InventorySlot(this);
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
//            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.STORE_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            inventorySlotTable.add(inventorySlot).size (slotWidth, slotHeight);

            if(i % lengthSlotRow == 0){
                inventorySlotTable.row();
            }
        }

        for(int i = 1; i <= InventoryUI._numSlots; i++){
            InventorySlot inventorySlot = new InventorySlot(this);
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
//            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.PLAYER_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            playerInventorySlotTable.add(inventorySlot).size (slotWidth, slotHeight);

            if(i % lengthSlotRow == 0){
                playerInventorySlotTable.row();
            }
        }

        inventoryActors.add(inventorySlotTooltip);

        this.add();
        this.add (closeButton);
        this.row();

        //this.debugAll();
        this.defaults().expand().fill();
        this.add(inventorySlotTable).pad(10, 10, 10, 10).row();
        this.add (buttons).row();
        this.add (totalLabels).row();
        this.add (playerInventorySlotTable).pad(10, 10, 10, 10).row();
        this.add (playerTotalGP);
        this.pack();

        //Listeners
        buyButton.addListener(new ClickListener() {
                                               @Override
                                               public void clicked(InputEvent event, float x, float y) {
                                                   if( fullValue > 0 && playerTotal >= fullValue) {
                                                       playerTotal -= fullValue;
//                                                       StoreInventoryUI.this.notify(Integer.toString (playerTotal), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                                                       hudScreen.updateTotalGoldPoint (playerTotal);
                                                       fullValue = 0;
                                                       buyTotalLabel.setText(BUY  + " : " +  fullValue + GP);

                                                       checkButtonStates();

                                                       //Make sure we update the owner of the items
                                                       InventoryUI.setInventoryItemNames (playerInventorySlotTable, InventoryUI.PLAYER_INVENTORY);
                                                       savePlayerInventory();
                                                   }
                                               }
                                           }
        );

        sellButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       if( tradeInVal > 0 ) {
                                           playerTotal += tradeInVal;
//                                           StoreInventoryUI.this.notify(Integer.toString (playerTotal), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                                           hudScreen.updateTotalGoldPoint (playerTotal);
                                           tradeInVal = 0;
                                           sellTotalLabel.setText(SELL  + " : " +  tradeInVal + GP);

                                           checkButtonStates();

                                           //Remove sold items
                                           Array<Cell> cells = inventorySlotTable.getCells();
                                           for( int i = 0; i < cells.size; i++){
                                               InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
                                               if( inventorySlot == null ) continue;
                                               if( inventorySlot.hasItem() &&
                                                       inventorySlot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)){
                                                   inventorySlot.clearAllInventoryItems(false);
                                               }
                                           }
                                           savePlayerInventory();
                                       }
                                   }
                                }
        );
    }

    public TextButton getCloseButton(){
        return closeButton;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    public Array<Actor> getInventoryActors(){
        return inventoryActors;
    }

    public void loadPlayerInventory(Array<InventoryItemLocation> playerInventoryItems){
        InventoryUI.populateInventory (playerInventorySlotTable, playerInventoryItems, dragAndDrop, InventoryUI.PLAYER_INVENTORY, true);
    }

    public void loadStoreInventory(Array<InventoryItemLocation> storeInventoryItems){
        InventoryUI.populateInventory(inventorySlotTable, storeInventoryItems, dragAndDrop, InventoryUI.STORE_INVENTORY, false);
    }

    public void savePlayerInventory(){
        Array<InventoryItemLocation> playerItemsInPlayerInventory = InventoryUI.getInventoryFiltered (playerInventorySlotTable, InventoryUI.STORE_INVENTORY);
        Array<InventoryItemLocation> playerItemsInStoreInventory = InventoryUI.getInventoryFiltered (playerInventorySlotTable, inventorySlotTable, InventoryUI.STORE_INVENTORY);
        playerItemsInPlayerInventory.addAll(playerItemsInStoreInventory);
//        StoreInventoryUI.this.notify(json.toJson(playerItemsInPlayerInventory), StoreInventoryEvent.PLAYER_INVENTORY_UPDATED);
        hudScreen.updateInventory(playerItemsInPlayerInventory);
    }

    public void cleanupStoreInventory(){
        InventoryUI.removeInventoryItems(InventoryUI.STORE_INVENTORY, playerInventorySlotTable);
        InventoryUI.removeInventoryItems(InventoryUI.PLAYER_INVENTORY, inventorySlotTable);
    }

    public void removedItem(InventorySlot slot) {
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
            tradeInVal -= slot.getTopInventoryItem().getTradeValue();
            sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
        }
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
            fullValue -= slot.getTopInventoryItem().getItemValue();
            buyTotalLabel.setText(BUY + " : " + fullValue + GP);
        }
        checkButtonStates();
    }

    public void addedItem(InventorySlot slot) {
        //moving from player inventory to store inventory to sell
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
            tradeInVal += slot.getTopInventoryItem().getTradeValue();
            sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
        }
        //moving from store inventory to player inventory to buy
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
            fullValue += slot.getTopInventoryItem().getItemValue();
            buyTotalLabel.setText(BUY + " : " +  fullValue + GP);
        }
        checkButtonStates();
    }

//    @Override
   /* public void onNotify(InventorySlot slot, SlotEvent event) {
        switch(event)
        {
            case ADDED_ITEM:
                //moving from player inventory to store inventory to sell
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
                    tradeInVal += slot.getTopInventoryItem().getTradeValue();
                    sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
                }
                //moving from store inventory to player inventory to buy
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
                    fullValue += slot.getTopInventoryItem().getItemValue();
                    buyTotalLabel.setText(BUY + " : " +  fullValue + GP);
                }
                break;
            case REMOVED_ITEM:
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
                    tradeInVal -= slot.getTopInventoryItem().getTradeValue();
                    sellTotalLabel.setText(SELL + " : " + tradeInVal + GP);
                }
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
                    fullValue -= slot.getTopInventoryItem().getItemValue();
                    buyTotalLabel.setText(BUY + " : " + fullValue + GP);
                }
                break;
        }
        checkButtonStates();
    }*/

    public void checkButtonStates(){
        if( tradeInVal <= 0 ) {
            disableButton (sellButton, true);
        }else{
            disableButton (sellButton, false);
        }

        if( fullValue <= 0 || playerTotal < fullValue) {
            disableButton (buyButton, true);
        }else{
            disableButton (buyButton, false);
        }
    }

    public void setPlayerGP(int value){
        playerTotal = value;
        playerTotalGP.setText(PLAYER_TOTAL + " : " + playerTotal +  GP);
    }

    private void disableButton(Button button, boolean disable){
        if( disable ){
            button.setDisabled(true);
            button.setTouchable(Touchable.disabled);
        }else{
            button.setDisabled(false);
            button.setTouchable(Touchable.enabled);
        }
    }

}
