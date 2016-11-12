package com.v5ent.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.v5ent.game.utils.Constants;
import com.badlogic.gdx.math.Rectangle;


public class WorldRenderer implements Disposable {

	private static final String TAG = WorldRenderer.class.getName();

//	private OrthographicCamera camera;
//	private SpriteBatch batch;
	private WorldController worldController;

	public WorldRenderer (WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init () {
//		batch = new SpriteBatch();
//		batch.setProjectionMatrix(worldController.camera.combined);
//		batch.begin();
	}

	public void render () {
		renderMap();
	}

	private void renderMap(){

		//mapRenderer.getBatch().enableBlending();
		//mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		worldController.mapRenderer.setView(worldController.camera);
		worldController.mapRenderer.render();
		worldController.mapRenderer.getBatch().begin();
		//地面
		TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.GROUND_LAYER);
		if( groundMapLayer != null){
			worldController.mapRenderer.renderTileLayer(groundMapLayer);
		}
		TiledMapTileLayer floorMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.FLOOR_LAYER);
		if( floorMapLayer != null){
			worldController.mapRenderer.renderTileLayer(floorMapLayer);
		}
		//blocks
		TiledMapTileLayer blockMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.BLOCK_LAYER);
		if( blockMapLayer != null && Gdx.app.getLogLevel()== Application.LOG_DEBUG){
			worldController.mapRenderer.renderTileLayer(blockMapLayer);
		}

		worldController.mapRenderer.getBatch().end();

		worldController.mapRenderer.getBatch().begin();

		worldController.player.draw(worldController.mapRenderer.getBatch());

		TiledMapTileLayer ceilMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.CEILING_LAYER);
		if( ceilMapLayer != null){
			worldController.mapRenderer.renderTileLayer(ceilMapLayer);
		}

		worldController.mapRenderer.getBatch().end();
	}


	public void resize (int width, int height) {
		worldController.camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		worldController.camera.update();
	}

	@Override
	public void dispose () {
//		batch.dispose();
	}

}
