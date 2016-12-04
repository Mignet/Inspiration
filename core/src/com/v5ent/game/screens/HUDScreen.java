package com.v5ent.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.entities.Role;
import com.v5ent.game.hud.DialogUI;
import com.v5ent.game.hud.InventoryUI;
import com.v5ent.game.hud.StatusUI;
import com.v5ent.game.inventory.InventoryItem;
import com.v5ent.game.inventory.InventoryItemLocation;
import com.v5ent.game.utils.Constants;

/**
 * Created by Mignet on 2016/11/26.
 */
public class HUDScreen implements Screen {
    private WorldController worldController;
    private Stage stage;
    private StatusUI statusUI;
    private DialogUI dialogUI;
    private InventoryUI inventoryUI;
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
        statusUI = new StatusUI();
        statusUI.setVisible(true);
        statusUI.setPosition(0,stage.getHeight()-statusUI.getHeight());
//        statusUI.setKeepWithinStage(false);
//        statusUI.setMovable(false);
        inventoryUI = new InventoryUI();
        inventoryUI.setKeepWithinStage(false);
        inventoryUI.setMovable(false);
        inventoryUI.setVisible(false);
        inventoryUI.setPosition(20, 0);
        initInventory(player);

        dialogUI = new DialogUI();
        dialogUI.setMovable(false);
        dialogUI.setModal(true);
        dialogUI.setVisible(false);
        dialogUI.setPosition(20, 10);
        dialogUI.setWidth(stage.getWidth()-40);
        dialogUI.setHeight(160);

        stage.addActor(statusUI);
        stage.addActor(inventoryUI);
        stage.addActor(dialogUI);

        statusUI.validate();
        dialogUI.validate();
        inventoryUI.validate();

        //add tooltips to the stage
        Array<Actor> actors = inventoryUI.getInventoryActors();
        for(Actor actor : actors){
            stage.addActor(actor);
        }

        //Listeners
        inventoryButton = statusUI.getInventoryButton();
        inventoryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                inventoryUI.setVisible(inventoryUI.isVisible() ? false : true);
                //显示即禁用
                /*if(_inventoryUI.isVisible()){
                    followCnt++;
                }else{
                    followCnt--;
                }
                if(followCnt>0){
                    _mapMgr.setFollowSwitch(false);
                }else{
                    _mapMgr.setFollowSwitch(true);
                }*/
            }
        });

        questButton = statusUI.getQuestButton();
        questButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
               /* _questUI.setVisible(_questUI.isVisible() ? false : true);
                //显示即禁用
                if(_questUI.isVisible()){
                    followCnt++;
                }else{
                    followCnt--;
                }
                if(followCnt>0){
                    _mapMgr.setFollowSwitch(false);
                }else{
                    _mapMgr.setFollowSwitch(true);
                }*/
            }
        });
        inventoryUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     inventoryUI.setVisible(false);
                     inventoryButton.setChecked(false);
//                     worldController.closeSpeech();
                 }
             }
        );
        dialogUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     worldController.closeSpeech();
                     dialogUI.setVisible(false);
                 }
             }
        );
    }

    private void initInventory(Role player) {
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
        statusUI.setStatusForLevel(1);
    }

    public void loadSpeech(Role npc){
        dialogUI.loadConversation(npc);
        dialogUI.setVisible(true);
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
