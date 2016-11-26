package com.v5ent.game.hud;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.v5ent.game.entities.Role;

/**
 * Created by Mignet on 2016/11/26.
 */
public class PlayerHUD implements Screen {
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    private Viewport viewport;
    public PlayerHUD(Camera camera, Role player) {
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
//        _statusUI = new StatusUI();
//        _inventoryUI = new InventoryUI();
//        _stage.addActor(_statusUI);
//        _stage.addActor(_inventoryUI);
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
