package com.v5ent.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.dialog.ConversationCommandEvent;
import com.v5ent.game.dialog.ConversationGraph;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.hud.ButtonUI;
import com.v5ent.game.hud.DialogUI;
import com.v5ent.game.hud.InventoryUI;
import com.v5ent.game.hud.StatusUI;
import com.v5ent.game.hud.StoreInventoryUI;
import com.v5ent.game.inventory.InventoryItem;
import com.v5ent.game.inventory.InventoryItemLocation;
import com.v5ent.game.utils.AssetsManager;
import com.v5ent.game.utils.Constants;

/**
 * Created by Mignet on 2016/11/26.
 */
public class HUDScreen implements Screen {
    private WorldController worldController;
    private Stage stage;
    private StatusUI statusUI;
    private ButtonUI buttonUI;
    /** speech */
    private DialogUI dialogUI;
    private InventoryUI inventoryUI;
    private StoreInventoryUI storeInventoryUI;
    private Dialog messageBoxUI;
    //buttons
    private final ImageButton inventoryButton;
    private final ImageButton questButton;

    public Stage getStage() {
        return stage;
    }

    private ExtendViewport viewport;
    public HUDScreen(WorldController controller, Role player) {
        worldController = controller;
        viewport = new ExtendViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT,controller.hudCamera);
        stage = new Stage(viewport);

        messageBoxUI = new Dialog("消息", AssetsManager.instance.STATUSUI_SKIN, "solidbackground"){
            {
                button("确定");
                text("INVENTORY_FULL");
            }
            @Override
            protected void result(final Object object){
                cancel();
                setVisible(false);
            }

        };

        messageBoxUI.setVisible(false);
        messageBoxUI.pack();
        messageBoxUI.setPosition(stage.getWidth() / 2 - messageBoxUI.getWidth() / 2, stage.getHeight() / 2 - messageBoxUI.getHeight() / 2);

        statusUI = new StatusUI();
        statusUI.setVisible(true);
        statusUI.setPosition(0,stage.getHeight()-statusUI.getHeight());
        buttonUI = new ButtonUI();
        buttonUI.setVisible(true);
        buttonUI.setPosition(20,0);
//        statusUI.setKeepWithinStage(false);
//        statusUI.setMovable(false);
        inventoryUI = new InventoryUI(this);
        inventoryUI.setKeepWithinStage(false);
        inventoryUI.setMovable(false);
        inventoryUI.setVisible(false);
        inventoryUI.setPosition(100, 0);

        storeInventoryUI = new StoreInventoryUI(this);
        storeInventoryUI.setMovable(false);
        storeInventoryUI.setVisible(false);
        storeInventoryUI.setModal(true);
        storeInventoryUI.setPosition(0, 0);

        dialogUI = new DialogUI(this);
        dialogUI.setMovable(false);
        dialogUI.setModal(true);
        dialogUI.setVisible(false);
        dialogUI.setPosition(20, 32);
        dialogUI.setWidth(stage.getWidth()-40);
        dialogUI.setHeight(160);

        stage.addActor(statusUI);
        stage.addActor(buttonUI);
        stage.addActor(inventoryUI);
        stage.addActor(storeInventoryUI);
        stage.addActor(dialogUI);
        stage.addActor(messageBoxUI);

        statusUI.validate();
        buttonUI.validate();
        dialogUI.validate();
        inventoryUI.validate();
        storeInventoryUI.validate();
        messageBoxUI.validate();

        //add tooltips to the stage
        Array<Actor> actors = inventoryUI.getInventoryActors();
        for(Actor actor : actors){
            stage.addActor(actor);
        }
        Array<Actor> storeActors = storeInventoryUI.getInventoryActors();
        for(Actor actor : storeActors ){
            stage.addActor(actor);
        }

        //Listeners
        inventoryButton = buttonUI.getInventoryButton();
        inventoryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                inventoryUI.setVisible(inventoryUI.isVisible() ? false : true);
            }
        });

        questButton = buttonUI.getQuestButton();
        questButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
               /* _questUI.setVisible(_questUI.isVisible() ? false : true);*/
            }
        });
        inventoryUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     inventoryUI.setVisible(false);
                     inventoryButton.setChecked(false);
                 }
             }
        );
        dialogUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     closeSpeech();
                 }
             }
        );
        storeInventoryUI.getCloseButton().addListener(new ClickListener() {
                                                           @Override
                                                           public void clicked(InputEvent event, float x, float y) {
                                                               storeInventoryUI.savePlayerInventory();
                                                               storeInventoryUI.cleanupStoreInventory();
                                                               storeInventoryUI.setVisible(false);
                                                           }
                                                       }
        );
        initPlayerDatas(player);
    }

    private void initPlayerDatas(Role player) {
        InventoryUI.clearInventoryItems(inventoryUI.getInventorySlotTable());
        InventoryUI.clearInventoryItems(inventoryUI.getEquipSlotTable());
        inventoryUI.resetEquipSlots();

//        questUI.setQuests(new Array<QuestGraph>());

        //add default items if first time
        Array<InventoryItem.ItemTypeID> items = player.getInventory();
        Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
        for( int i = 0; i < items.size; i++){
            itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
        }
        InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), itemLocations, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
//        profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.getInventorySlotTable()));

        //start the player with some money
        statusUI.setGoldValue(2000);
//        statusUI.setStatusForLevel(1);
    }
/***************************************************SPEECH EVENT*************************************************************/
    public void executeCommandEvent(ConversationGraph graph, ConversationCommandEvent event){
        switch(event) {
            case LOAD_STORE_INVENTORY:
                openStore();
                break;
            case EXIT_CONVERSATION:
                closeSpeech();
                break;
            case INN_SLEEP:
                sleepInHotel();
                break;
            case ACCEPT_QUEST:
                acceptQuest();
                break;
            case RETURN_QUEST:
                returnQuest();
                break;
            case ADD_ENTITY_TO_INVENTORY:
                pickUpItem();
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
    /**
     * LOAD_STORE_INVENTORY
      */
    public void openStore(){
        Npc selectedEntity = worldController.getCurrentSelectedNpc();
        if( selectedEntity == null ){
            return;
        }

        Array<InventoryItemLocation> inventory =  InventoryUI.getInventory(inventoryUI.getInventorySlotTable());
        storeInventoryUI.loadPlayerInventory(inventory);

        Array<InventoryItem.ItemTypeID> items  = selectedEntity.getInventory();
        Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
        for( int i = 0; i < items.size; i++){
            itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.STORE_INVENTORY));
        }

        storeInventoryUI.loadStoreInventory(itemLocations);

        dialogUI.setVisible(false);
        storeInventoryUI.toFront();
        storeInventoryUI.setVisible(true);
   }

    /**
     * ACCEPT_QUEST
     */
    public void acceptQuest(){
        Npc currentlySelectedEntity = worldController.getCurrentSelectedNpc();
        if( currentlySelectedEntity == null ){
            return;
        }
//        EntityConfig config = currentlySelectedEntity.getEntityConfig();
//
//        QuestGraph questGraph = questUI.loadQuest(config.getQuestConfigPath());

//        if( questGraph != null ){
            //Update conversation dialog
//            config.setConversationConfigPath(QuestUI.RETURN_QUEST);
//            config.setCurrentQuestID(questGraph.getQuestID());
//            ProfileManager.getInstance().setProperty(config.getEntityID(), config);
//            updateEntityObservers();
//        }

        closeSpeech();
    }

    /**
     * RETURN_QUEST
     */
    public void returnQuest(){
        Npc returnEntity = worldController.getCurrentSelectedNpc();
        if( returnEntity == null ){
            return;
        }
//        EntityConfig configReturn = returnEntity.getEntityConfig();

//        EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(configReturn.getEntityID(), EntityConfig.class);
//        if( configReturnProperty == null ) return;

       /* String questID = configReturnProperty.getCurrentQuestID();

        if( _questUI.isQuestReadyForReturn(questID) ){
            notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
            QuestGraph quest = _questUI.getQuestByID(questID);
            _statusUI.addXPValue(quest.getXpReward());
            _statusUI.addGoldValue(quest.getGoldReward());
            notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
            _inventoryUI.removeQuestItemFromInventory(questID);
            configReturnProperty.setConversationConfigPath(QuestUI.FINISHED_QUEST);
            ProfileManager.getInstance().setProperty(configReturnProperty.getEntityID(), configReturnProperty);
        }*/

        closeSpeech();
    }

    /**
     * ADD_ENTITY_TO_INVENTORY
     */
    public void pickUpItem(){
        Npc entity = worldController.getCurrentSelectedNpc();
        if( entity == null ){
            return;
        }

        if( inventoryUI.doesInventoryHaveSpace() ){
//            inventoryUI.addEntityToInventory(entity, entity.getCurrentQuestID());
//            _mapMgr.clearCurrentSelectedMapEntity();
            dialogUI.setVisible(false);
//            entity.unregisterObservers();
//            worldController.removeMapQuestEntity(entity);
//            _questUI.updateQuests(_mapMgr);
        }else{
            closeSpeech();
            messageBoxUI.setVisible(true);
        }
    }
    /**
     * EXIT_CONVERSATION
     */
    public void closeSpeech(){
        dialogUI.setVisible(false);
        worldController.closeSpeechWithNpc();
    }

    /**
     * INN_SLEEP
     */
    public void sleepInHotel(){
        //health++,money--
        statusUI.setHPValue(statusUI.getHPValueMax());
        statusUI.setGoldValue(statusUI.getGoldValue()-10);
    }

    /**
     * speech
     * @param npc
     */
    public void loadSpeech(Role npc){
        dialogUI.loadConversation(npc);
        dialogUI.setVisible(true);
    }
/******************************************************* EVENT ************************************************************/
    /**
     * ITEM CONSUMED
     * @param type
     * @param typeValue
     */
    public void consumeItem(int type,int typeValue){
        if( InventoryItem.doesRestoreHP(type) ){
//            notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_EATING);
            statusUI.addHPValue(typeValue);
        }else if( InventoryItem.doesRestoreMP(type) ){
//            notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
            statusUI.addMPValue(typeValue);
        }
    }

    /**
     * PLAYER_GP_TOTAL_UPDATED
     * @param gold
     */
    public void updateGoldPoint(int gold){
        statusUI.setGoldValue(gold);
//        notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
    }

    /**
     * PLAYER_INVENTORY_UPDATED:
     * @param items
     */
    public void updateInventory(Array<InventoryItemLocation> items){
        InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), items, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
