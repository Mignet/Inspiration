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
    private int _numStoreInventorySlots = 36;
    private int _lengthSlotRow = 12;
    private Table inventorySlotTable;
    private Table _playerInventorySlotTable;
    private DragAndDrop dragAndDrop;
    private Array<Actor> inventoryActors;

    private final int _slotWidth = 46;
    private final int _slotHeight = 46;

    private InventorySlotTooltip inventorySlotTooltip;

    private Label _sellTotalLabel;
    private Label _buyTotalLabel;
    private Label _playerTotalGP;

    private int _tradeInVal = 0;
    private int _fullValue = 0;
    private int _playerTotal = 0;

    private Button _sellButton;
    private Button _buyButton;
    public TextButton _closeButton;

    private Table _buttons;
    private Table _totalLabels;

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

        _playerInventorySlotTable = new Table();
        _playerInventorySlotTable.setName(InventoryUI.PLAYER_INVENTORY);
        inventorySlotTooltip = new InventorySlotTooltip(Assets.instance.STATUSUI_SKIN);

        _sellButton = new TextButton(SELL, Assets.instance.STATUSUI_SKIN, "inventory");
        disableButton(_sellButton, true);

        _sellTotalLabel = new Label(SELL + " : " + _tradeInVal + GP, Assets.instance.STATUSUI_SKIN);
        _sellTotalLabel.setAlignment(Align.center);
        _buyTotalLabel = new Label(BUY + " : " + _fullValue + GP, Assets.instance.STATUSUI_SKIN);
        _buyTotalLabel.setAlignment(Align.center);

        _playerTotalGP = new Label(PLAYER_TOTAL + " : " + _playerTotal +  GP, Assets.instance.STATUSUI_SKIN);

        _buyButton = new TextButton(BUY, Assets.instance.STATUSUI_SKIN, "inventory");
        disableButton(_buyButton, true);

        _closeButton = new TextButton("X", Assets.instance.STATUSUI_SKIN);

        _buttons = new Table();
        _buttons.defaults().expand().fill();
        _buttons.add(_sellButton).padLeft(10).padRight(10);
        _buttons.add(_buyButton).padLeft(10).padRight(10);

        _totalLabels = new Table();
        _totalLabels.defaults().expand().fill();
        _totalLabels.add(_sellTotalLabel).padLeft(40);
        _totalLabels.add();
        _totalLabels.add(_buyTotalLabel).padRight(40);

        //layout
        for(int i = 1; i <= _numStoreInventorySlots; i++){
            InventorySlot inventorySlot = new InventorySlot(this);
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
//            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.STORE_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            inventorySlotTable.add(inventorySlot).size(_slotWidth, _slotHeight);

            if(i % _lengthSlotRow == 0){
                inventorySlotTable.row();
            }
        }

        for(int i = 1; i <= InventoryUI._numSlots; i++){
            InventorySlot inventorySlot = new InventorySlot(this);
            inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
//            inventorySlot.addObserver(this);
            inventorySlot.setName(InventoryUI.PLAYER_INVENTORY);

            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            _playerInventorySlotTable.add(inventorySlot).size(_slotWidth, _slotHeight);

            if(i % _lengthSlotRow == 0){
                _playerInventorySlotTable.row();
            }
        }

        inventoryActors.add(inventorySlotTooltip);

        this.add();
        this.add(_closeButton);
        this.row();

        //this.debugAll();
        this.defaults().expand().fill();
        this.add(inventorySlotTable).pad(10, 10, 10, 10).row();
        this.add(_buttons).row();
        this.add(_totalLabels).row();
        this.add(_playerInventorySlotTable).pad(10, 10, 10, 10).row();
        this.add(_playerTotalGP);
        this.pack();

        //Listeners
        _buyButton.addListener(new ClickListener() {
                                               @Override
                                               public void clicked(InputEvent event, float x, float y) {
                                                   if( _fullValue > 0 && _playerTotal >= _fullValue) {
                                                       _playerTotal -= _fullValue;
//                                                       StoreInventoryUI.this.notify(Integer.toString(_playerTotal), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                                                       hudScreen.updateTotalGoldPoint(_playerTotal);
                                                       _fullValue = 0;
                                                       _buyTotalLabel.setText(BUY  + " : " +  _fullValue + GP);

                                                       checkButtonStates();

                                                       //Make sure we update the owner of the items
                                                       InventoryUI.setInventoryItemNames(_playerInventorySlotTable, InventoryUI.PLAYER_INVENTORY);
                                                       savePlayerInventory();
                                                   }
                                               }
                                           }
        );

        _sellButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       if( _tradeInVal > 0 ) {
                                           _playerTotal += _tradeInVal;
//                                           StoreInventoryUI.this.notify(Integer.toString(_playerTotal), StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED);
                                           hudScreen.updateTotalGoldPoint(_playerTotal);
                                           _tradeInVal = 0;
                                           _sellTotalLabel.setText(SELL  + " : " +  _tradeInVal + GP);

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
        return _closeButton;
    }

    public Table getInventorySlotTable() {
        return inventorySlotTable;
    }

    public Array<Actor> getInventoryActors(){
        return inventoryActors;
    }

    public void loadPlayerInventory(Array<InventoryItemLocation> playerInventoryItems){
        InventoryUI.populateInventory(_playerInventorySlotTable, playerInventoryItems, dragAndDrop, InventoryUI.PLAYER_INVENTORY, true);
    }

    public void loadStoreInventory(Array<InventoryItemLocation> storeInventoryItems){
        InventoryUI.populateInventory(inventorySlotTable, storeInventoryItems, dragAndDrop, InventoryUI.STORE_INVENTORY, false);
    }

    public void savePlayerInventory(){
        Array<InventoryItemLocation> playerItemsInPlayerInventory = InventoryUI.getInventoryFiltered(_playerInventorySlotTable, InventoryUI.STORE_INVENTORY);
        Array<InventoryItemLocation> playerItemsInStoreInventory = InventoryUI.getInventoryFiltered(_playerInventorySlotTable, inventorySlotTable, InventoryUI.STORE_INVENTORY);
        playerItemsInPlayerInventory.addAll(playerItemsInStoreInventory);
//        StoreInventoryUI.this.notify(json.toJson(playerItemsInPlayerInventory), StoreInventoryEvent.PLAYER_INVENTORY_UPDATED);
        hudScreen.updateInventory(playerItemsInPlayerInventory);
    }

    public void cleanupStoreInventory(){
        InventoryUI.removeInventoryItems(InventoryUI.STORE_INVENTORY, _playerInventorySlotTable);
        InventoryUI.removeInventoryItems(InventoryUI.PLAYER_INVENTORY, inventorySlotTable);
    }

    public void removedItem(InventorySlot slot) {
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
            _tradeInVal -= slot.getTopInventoryItem().getTradeValue();
            _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP);
        }
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
            _fullValue -= slot.getTopInventoryItem().getItemValue();
            _buyTotalLabel.setText(BUY + " : " + _fullValue + GP);
        }
        checkButtonStates();
    }

    public void addedItem(InventorySlot slot) {
        //moving from player inventory to store inventory to sell
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
            _tradeInVal += slot.getTopInventoryItem().getTradeValue();
            _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP);
        }
        //moving from store inventory to player inventory to buy
        if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
            _fullValue += slot.getTopInventoryItem().getItemValue();
            _buyTotalLabel.setText(BUY + " : " +  _fullValue + GP);
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
                    _tradeInVal += slot.getTopInventoryItem().getTradeValue();
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP);
                }
                //moving from store inventory to player inventory to buy
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
                    _fullValue += slot.getTopInventoryItem().getItemValue();
                    _buyTotalLabel.setText(BUY + " : " +  _fullValue + GP);
                }
                break;
            case REMOVED_ITEM:
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) ) {
                    _tradeInVal -= slot.getTopInventoryItem().getTradeValue();
                    _sellTotalLabel.setText(SELL + " : " + _tradeInVal + GP);
                }
                if( slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY) &&
                        slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY) ) {
                    _fullValue -= slot.getTopInventoryItem().getItemValue();
                    _buyTotalLabel.setText(BUY + " : " + _fullValue + GP);
                }
                break;
        }
        checkButtonStates();
    }*/

    public void checkButtonStates(){
        if( _tradeInVal <= 0 ) {
            disableButton(_sellButton, true);
        }else{
            disableButton(_sellButton, false);
        }

        if( _fullValue <= 0 || _playerTotal < _fullValue) {
            disableButton(_buyButton, true);
        }else{
            disableButton(_buyButton, false);
        }
    }

    public void setPlayerGP(int value){
        _playerTotal = value;
        _playerTotalGP.setText(PLAYER_TOTAL + " : " + _playerTotal +  GP);
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
