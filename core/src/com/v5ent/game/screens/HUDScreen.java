package com.v5ent.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.v5ent.game.battle.BattleEvent;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.dialog.ConversationCommandEvent;
import com.v5ent.game.dialog.ConversationGraph;
import com.v5ent.game.entities.Monster;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.hud.BattleUI;
import com.v5ent.game.hud.ButtonUI;
import com.v5ent.game.hud.DialogUI;
import com.v5ent.game.hud.InventoryUI;
import com.v5ent.game.hud.QuestUI;
import com.v5ent.game.hud.StatusUI;
import com.v5ent.game.hud.StoreInventoryUI;
import com.v5ent.game.inventory.InventoryItem;
import com.v5ent.game.inventory.InventoryItemLocation;
import com.v5ent.game.quest.QuestGraph;
import com.v5ent.game.sfx.ScreenTransitionAction;
import com.v5ent.game.sfx.ScreenTransitionActor;
import com.v5ent.game.sfx.ShakeCamera;
import com.v5ent.game.utils.Assets;
import com.v5ent.game.utils.Constants;

/**
 * Created by Mignet on 2016/11/26.
 */
public class HUDScreen implements Screen {
    private Stage stage;
    private WorldController worldController;
    private ShakeCamera shakeCam;
    private StatusUI statusUI;
    private ButtonUI buttonUI;
    /** speech */
    private DialogUI dialogUI;
    private InventoryUI inventoryUI;
    private StoreInventoryUI storeInventoryUI;
    private QuestUI questUI;
    public BattleUI battleUI;
    /** message box */
    private Dialog messageBoxUI;
    //buttons
    private final ImageButton inventoryButton;
    private final ImageButton questButton;

    public Stage getStage() {
        return stage;
    }

    public HUDScreen(WorldController controller, Role player) {
        worldController = controller;
        ExtendViewport viewport = new ExtendViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT,controller.hudCamera);
        stage = new Stage(viewport);
        transitionActor = new ScreenTransitionActor();
        shakeCam = new ShakeCamera(0,0, 30.0f);
        messageBoxUI = new Dialog("消息", Assets.instance.STATUSUI_SKIN, "solidbackground"){
            {
                button("确定");
                text("包裹已满！");
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

        statusUI = new StatusUI(this);
        statusUI.setVisible(true);
        statusUI.setPosition(stage.getWidth()-statusUI.getWidth(),stage.getHeight()-statusUI.getHeight());
        buttonUI = new ButtonUI();
        buttonUI.setVisible(true);
        buttonUI.setPosition(20,0);

        battleUI = new BattleUI(this,player);
        battleUI.setMovable(false);
        //removes all listeners including ones that handle focus
//        battleUI.clearListeners();
        battleUI.setModal(true);
        battleUI.setVisible(false);

        inventoryUI = new InventoryUI(this);
        inventoryUI.setKeepWithinStage(false);
        inventoryUI.setMovable(false);
        inventoryUI.setVisible(false);
        inventoryUI.setModal(true);
        inventoryUI.setPosition(76, 0);

        storeInventoryUI = new StoreInventoryUI(this);
        storeInventoryUI.setMovable(false);
        storeInventoryUI.setVisible(false);
        storeInventoryUI.setModal(true);
        storeInventoryUI.setPosition(0, 0);

        questUI = new QuestUI();
        questUI.setMovable(false);
        questUI.setModal(true);
        questUI.setVisible(false);
        questUI.setKeepWithinStage(false);
        questUI.setPosition(0, 0);
        questUI.setWidth(stage.getWidth());
        questUI.setHeight(stage.getHeight());

        dialogUI = new DialogUI(this);
        dialogUI.setMovable(false);
        dialogUI.setModal(true);
        dialogUI.setVisible(false);
        dialogUI.setPosition(20, 32);
        dialogUI.setWidth(stage.getWidth()-40);
        dialogUI.setHeight(160);

        stage.addActor(battleUI);
        stage.addActor(statusUI);
        stage.addActor(questUI);
        stage.addActor(buttonUI);
        stage.addActor(inventoryUI);
        stage.addActor(storeInventoryUI);
        stage.addActor(dialogUI);
        stage.addActor(messageBoxUI);

        battleUI.validate();
        statusUI.validate();
        buttonUI.validate();
        dialogUI.validate();
        inventoryUI.validate();
        storeInventoryUI.validate();
        questUI.validate();
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
                questUI.setVisible(questUI.isVisible() ? false : true);
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
        questUI.getCloseButton().addListener(new ClickListener() {
                                                           @Override
                                                           public void clicked(InputEvent event, float x, float y) {
                                                               questUI.setVisible(false);
                                                               questButton.setChecked(false);
                                                           }
                                                       }
        );
        initPlayerDatas(player);
    }

    private void initPlayerDatas(Role player) {
        InventoryUI.clearInventoryItems(inventoryUI.getInventorySlotTable());
        InventoryUI.clearInventoryItems(inventoryUI.getEquipSlotTable());
        inventoryUI.resetEquipSlots();

        questUI.setQuests(new Array<QuestGraph>());

        //add default items if first time
        Array<InventoryItem.ItemTypeID> items = player.getInventory();
        Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
        for( int i = 0; i < items.size; i++){
            itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
        }
        InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), itemLocations, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
//        profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.getInventorySlotTable()));

        //start the player with some money
        statusUI.setGoldValue(player.getGoldPoint());
//        statusUI.setStatusForLevel(1);
    }
/*************************************************** Conversation Command Event *************************************************************/
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

        if( questUI.isQuestReadyForReturn(questID) ){
            notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
            QuestGraph quest = questUI.getQuestByID(questID);
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
//            questUI.updateQuests(_mapMgr);
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
        worldController.player.setHealthPoint(statusUI.getHPValueMax());
        statusUI.setHPValue(statusUI.getHPValueMax());
        statusUI.setGoldValue(statusUI.getGoldValue()-10);
//        messageBoxUI.clear();
//        messageBoxUI.text("You HP is resrmed,and cost 10 GoldPoint!").setWidth(stage.getWidth());
//        messageBoxUI.setVisible(true);
    }

    /**
     * LOAD_CONVERSATION
     * @param npc
     */
    public void loadSpeech(Role npc){
        dialogUI.loadConversation(npc);
        dialogUI.setVisible(true);
    }
    /***********************************/
    /**
     * ENEMY_SPAWN_LOCATION_CHANGED:
     */
    public void enterBattleZone(String value){
        String enemyZoneID = value;
        battleUI.battleZoneTriggered(Integer.parseInt(enemyZoneID));
        if(worldController.player.battleZoneSteps>=5){
            enterBattle();
            worldController.player.battleZoneSteps = 0;
        }
    }
    /**
     * PLAYER_HAS_MOVED
     * when player move some steps,start fight
     */
    public void enterBattle() {
        if (battleUI.isBattleReady()) {
            addTransitionToScreen();
//            MainGameScreen.setGameState(MainGameScreen.GameState.SAVING);
//            worldController.mapMgr.disableCurrentmapMusic();
//            notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
            battleUI.toBack();
            battleUI.setVisible(true);
        }
    }
/******************************************************* Inventory Event ************************************************************/
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
/******************************************************* StoreInventory Event ************************************************************/
    /**
     * PLAYER_GP_TOTAL_UPDATED
     * @param gold
     */
    public void updateTotalGoldPoint(int gold){
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
/******************************************************* Status Event *******************************************************************/
    /**
     * UPDATED_GP
      * @param value
     */
    public void updateGP(int value){
        storeInventoryUI.setPlayerGP(value);
//        ProfileManager.getInstance().setProperty("currentPlayerGP", statusUI.getGoldValue());
    }
    /**
     * UPDATED_HP
      * @param value
     */
    public void updateHP(int value){
//        ProfileManager.getInstance().setProperty("currentPlayerHP", statusUI.getHPValue());
    }
    /**
     * UPDATED_MP
      * @param value
     */
    public void updateMP(int value){
//        ProfileManager.getInstance().setProperty("currentPlayerMP", statusUI.getMPValue());
    }
    /**
     * UPDATED_XP
      * @param value
     */
    public void updateXP(int value){
//        ProfileManager.getInstance().setProperty("currentPlayerXP", statusUI.getXPValue());
    }
    /**
     * UPDATED_LEVEL
      * @param value
     */
    public void updateLevel(int value){
//        ProfileManager.getInstance().setProperty("currentPlayerLevel", _statusUI.getLevelValue());
    }
    /**
     * LEVELED_UP
     */
    public void levelUp(){
//        notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
    }
    /********************************************************************************************************************/
    public void onNotify(Monster enemyEntity, BattleEvent event) {
        switch (event) {
            case OPPONENT_HIT_DAMAGE:
//                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN);
                break;
            case OPPONENT_DEFEATED:
//                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
//                int goldReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_GP_REWARD.toString()));
                int goldReward = enemyEntity.getGpReward();
                statusUI.addGoldValue(goldReward);
//                int xpReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString()));
                int xpReward = enemyEntity.getXpReward();
                statusUI.addXPValue(xpReward);
//                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
//                mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                battleUI.setVisible(false);
                break;
            case PLAYER_RUNNING:
//                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
//                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
//                _mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                battleUI.setVisible(false);
                break;
            case PLAYER_HIT_DAMAGE:
//                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_PAIN);
//                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                int hpVal = worldController.player.getHealthPoint();
                statusUI.setHPValue(hpVal);
                shakeCam.startShaking();

                if( hpVal <= 0 ){
                    shakeCam.reset();
//                    notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                    addTransitionToScreen();
                    battleUI.setVisible(false);
//                    messageBoxUI.text("你死了!").setWidth(stage.getWidth());
//                    messageBoxUI.setVisible(true);
                    worldController.setGameOver(true);
                }
                break;
            case PLAYER_USED_MAGIC:
//                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_WAND_ATTACK);
//                int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
                int mpVal = worldController.player.getMagicPoint();
                statusUI.setMPValue(mpVal);
                break;
            default:
                break;
        }
    }
    private ScreenTransitionActor transitionActor;
    public void addTransitionToScreen(){
        transitionActor.setVisible(true);
        stage.addAction(
                Actions.sequence(
                        Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 1), transitionActor)));
    }
    @Override
    public void show() {
        shakeCam.reset();
    }

    @Override
    public void render(float delta) {
        if( shakeCam.isCameraShaking() ){
            Vector2 shakeCoords = shakeCam.getNewShakePosition();
            worldController.camera.position.x = shakeCoords.x + stage.getWidth()/2;
            worldController.camera.position.y = shakeCoords.y + stage.getHeight()/2;
        }
        if(worldController._enemySpawnID!=null){
            enterBattleZone(worldController._enemySpawnID);
        }
        if(worldController.player.getHealthPoint()>0){
            int hpVal = worldController.player.getHealthPoint();
            statusUI.setHPValue(hpVal);
        }
//        Object mapName = mapMgr.getCurrentTiledMap().getProperties().get("mapName");
//        _mapName.setText(mapName!=null?mapName.toString():"Unkonwn");
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        battleUI.resetDefaults();
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
