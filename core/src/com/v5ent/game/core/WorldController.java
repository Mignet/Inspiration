package com.v5ent.game.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.v5ent.game.entities.Role;
import com.v5ent.game.utils.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();

    public Role player;

    public OrthogonalTiledMapRenderer mapRenderer = null;
    public OrthographicCamera camera = null;
    public MapsManager mapMgr;

    public WorldController() {
        init();
    }

    private void init() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        mapMgr = new MapsManager();
        mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentMap());
        mapRenderer.setView(camera);
        camera.update();

        Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());

        player = new Role("001");
        player.setPosInMap(mapMgr.START_POINT);

        Gdx.input.setInputProcessor(this);
    }

    public void update(float deltaTime) {
        //updatePortalLayerActivation(player.boundingBox);

        player.update(deltaTime);
        handleDebugInput(deltaTime);
        //Preferable to lock and center the camera to the player's position
        camera.position.set(player.getX(), player.getY(), 0f);
        camera.update();
    }


    private void handleDebugInput(float delta) {
        if (Gdx.app.getType() != ApplicationType.Desktop) return;

        // Selected Sprite Controls When Idle
        if (player.getState() == Role.State.IDLE) {
            int x = MathUtils.floor(player.getX() / 32);
            int y = MathUtils.floor(player.getY() / 32);
            //Keyboard input
            if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
                //Gdx.app.debug(TAG, "LEFT key");
                //Collision Test
                if (!isCollisionWithMapLayer(x - 1, y)) {
                    player.moveOneStep(Role.Direction.LEFT);
                } else {
                    player.setCurrentDir(Role.Direction.LEFT);
                }
            } else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                //Gdx.app.debug(TAG, "RIGHT key");
                if (!isCollisionWithMapLayer(x + 1, y)) {
                    player.moveOneStep(Role.Direction.RIGHT);
                } else {
                    player.setCurrentDir(Role.Direction.RIGHT);
                }
            } else if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
                //Gdx.app.debug(TAG, "UP key");
                if (!isCollisionWithMapLayer(x, y + 1)) {
                    player.moveOneStep(Role.Direction.UP);
                } else {
                    player.setCurrentDir(Role.Direction.UP);
                }
            } else if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
                //Gdx.app.debug(TAG, "DOWN key");
                if (!isCollisionWithMapLayer(x, y - 1)) {
                    player.moveOneStep(Role.Direction.DOWN);
                } else {
                    player.setCurrentDir(Role.Direction.DOWN);
                }
            } else if (Gdx.input.isKeyPressed(Keys.Q)) {
                Gdx.app.exit();
            } else {
//			player.setState(Role.State.IDLE);
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        // Reset game world
        if (keycode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }
        return false;
    }

    private boolean isCollisionWithMapLayer(int x, int y) {
        TiledMapTileLayer mapCollisionLayer = mapMgr.getBlockLayer();

        if (mapCollisionLayer == null) {
            return false;
        }

        if (mapCollisionLayer.getCell(x, y) != null) {
            Gdx.app.debug(TAG, "Map Collision at = "+x+","+y);
            return true;
        }

        return false;
    }

}
