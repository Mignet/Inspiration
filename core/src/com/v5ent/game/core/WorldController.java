package com.v5ent.game.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.v5ent.game.entities.Player;
import com.v5ent.game.utils.Constants;

public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();

	public Player player;

	public OrthogonalTiledMapRenderer mapRenderer = null;
	public OrthographicCamera camera = null;
	public MapsManager mapMgr;

	public WorldController () {
		init();
	}

	private void init () {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();

		mapMgr = new MapsManager();
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentMap());
		mapRenderer.setView(camera);

		Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());

		player = new Player("001");
		player.setPosInMap(mapMgr.START_POINT);

		initTestObjects();
	}

	private void initTestObjects () {
		// Create new array for 5 sprites
	}

	public void update (float deltaTime) {
		player.update(deltaTime);
		handleDebugInput(deltaTime);
		//Preferable to lock and center the camera to the player's position
		camera.position.set(player.getX(), player.getY(), 0f);
		camera.update();
	}


	private void handleDebugInput (float delta) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		// Selected Sprite Controls
		//Keyboard input
		if( Gdx.input.isKeyPressed(Keys.A)){
			//Gdx.app.debug(TAG, "LEFT key");
			player.setState(Player.State.WALKING);
			player.setDirection(Player.Direction.LEFT, delta);
		}else if( Gdx.input.isKeyPressed(Keys.D)){
			//Gdx.app.debug(TAG, "RIGHT key");
			player.setState(Player.State.WALKING);
			player.setDirection(Player.Direction.RIGHT, delta);
		}else if( Gdx.input.isKeyPressed(Keys.W)){
			//Gdx.app.debug(TAG, "UP key");
			player.setState(Player.State.WALKING);
			player.setDirection(Player.Direction.UP, delta);
		}else if(Gdx.input.isKeyPressed(Keys.S)){
			//Gdx.app.debug(TAG, "DOWN key");
			player.setState(Player.State.WALKING);
			player.setDirection(Player.Direction.DOWN, delta);
		}else if(Gdx.input.isKeyPressed(Keys.Q)){
			Gdx.app.exit();
		}else{
			player.setState(Player.State.IDLE);
		}
	}

	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		return false;
	}
}
