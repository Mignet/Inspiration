package com.v5ent.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.hud.ConversationUI;
import com.v5ent.game.hud.StatusUI;

/**
 * Created by Mignet on 2016/11/26.
 */
public class HUDScreen implements Screen {
    private Stage stage;
    private ConversationUI conversationUI;
    private StatusUI _statusUI;

    public Stage getStage() {
        return stage;
    }

    private Viewport viewport;
    public HUDScreen(Camera camera, Role player) {
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
        _statusUI = new StatusUI();
        _statusUI.setVisible(true);
        _statusUI.setPosition(0, 0);
        _statusUI.setKeepWithinStage(false);
        _statusUI.setMovable(false);
//        _inventoryUI = new InventoryUI();
        conversationUI = new ConversationUI();
        conversationUI.setMovable(true);
        conversationUI.setVisible(false);
        conversationUI.setPosition(stage.getWidth() / 2, 0);
        conversationUI.setWidth(stage.getWidth() / 2);
        conversationUI.setHeight(stage.getHeight() / 2);

        stage.addActor(_statusUI);
//        _stage.addActor(_inventoryUI);
        stage.addActor(conversationUI);

        _statusUI.validate();
        conversationUI.validate();

        //Listeners
        ImageButton inventoryButton = _statusUI.getInventoryButton();
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

        ImageButton questButton = _statusUI.getQuestButton();
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
        conversationUI.getCloseButton().addListener(new ClickListener() {
                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     conversationUI.setVisible(false);
                 }
             }
        );
    }
    public void loadSpeech(Role npc){
        conversationUI.loadConversation(npc);
        conversationUI.setVisible(true);
    }
    public void hideSpeech(){
        conversationUI.setVisible(false);
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
