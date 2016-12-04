package com.v5ent.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.v5ent.game.entities.Role;
import com.v5ent.game.hud.DialogUI;
import com.v5ent.game.hud.StatusUI;
import com.v5ent.game.utils.Constants;

/**
 * Created by Mignet on 2016/11/26.
 */
public class HUDScreen implements Screen {
    private Stage stage;
    private DialogUI dialogUI;
    private StatusUI statusUI;

    public Stage getStage() {
        return stage;
    }

    private ExtendViewport viewport;
    public HUDScreen(Camera camera, Role player) {
        viewport = new ExtendViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT,camera);
        stage = new Stage(viewport);
        statusUI = new StatusUI();
        statusUI.setVisible(true);
        statusUI.setPosition(0,stage.getHeight()-statusUI.getHeight());
//        statusUI.setKeepWithinStage(false);
//        statusUI.setMovable(false);
//        _inventoryUI = new InventoryUI();
        dialogUI = new DialogUI();
        dialogUI.setMovable(false);
        dialogUI.setModal(true);
        dialogUI.setVisible(false);
        dialogUI.setPosition(20, 10);
        dialogUI.setWidth(stage.getWidth()-40);
        dialogUI.setHeight(160);

        stage.addActor(statusUI);
//        _stage.addActor(_inventoryUI);
        stage.addActor(dialogUI);

        statusUI.validate();
        dialogUI.validate();

        //Listeners
        ImageButton inventoryButton = statusUI.getInventoryButton();
        inventoryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
               /* _inventoryUI.setVisible(_inventoryUI.isVisible() ? false : true);
                //显示即禁用
                if(_inventoryUI.isVisible()){
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

        ImageButton questButton = statusUI.getQuestButton();
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
        dialogUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     dialogUI.setVisible(false);
                 }
             }
        );
    }
    public void loadSpeech(Role npc){
        dialogUI.loadConversation(npc);
        dialogUI.setVisible(true);
    }
    public void hideSpeech(){
        dialogUI.setVisible(false);
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
