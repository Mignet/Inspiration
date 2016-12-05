package com.v5ent.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.utils.Constants;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class WorldRenderer implements Disposable {

	private static final String TAG = WorldRenderer.class.getName();

//	private OrthographicCamera camera;
//	private SpriteBatch batch;
	private WorldController worldController;
	public OrthogonalTiledMapRenderer mapRenderer = null;

	public WorldRenderer (WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init () {
		mapRenderer = new OrthogonalTiledMapRenderer(worldController.mapMgr.getCurrentMap());
		mapRenderer.setView(worldController.camera);
		Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());
	}

	public void render () {
		renderMap();
	}

	private void renderMap(){

		//mapRenderer.getBatch().enableBlending();
		//mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		mapRenderer.setView(worldController.camera);
		mapRenderer.render();
		mapRenderer.getBatch().begin();
		//地面
		TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.GROUND_LAYER);
		if( groundMapLayer != null){
			mapRenderer.renderTileLayer(groundMapLayer);
		}
		TiledMapTileLayer floorMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.FLOOR_LAYER);
		if( floorMapLayer != null){
			mapRenderer.renderTileLayer(floorMapLayer);
		}
		//blocks
		TiledMapTileLayer blockMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.BLOCK_LAYER);
		if( blockMapLayer != null && Gdx.app.getLogLevel()== Application.LOG_DEBUG){
			mapRenderer.renderTileLayer(blockMapLayer);
		}
		mapRenderer.getBatch().end();

		mapRenderer.getBatch().begin();
		//target
		if(worldController.target!=null){
			worldController.target.draw(mapRenderer.getBatch());
		}
		//show roles order by Y-axis
		List<Sprite> temp =new ArrayList<Sprite>();
		temp.add(worldController.player);
		temp.addAll(worldController.mapMgr.npcs);
		Collections.sort(temp, new Comparator<Sprite>() {
			@Override
			public int compare(Sprite lhs, Sprite rhs) {
				// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
				return lhs.getY() > rhs.getY() ? -1 : (lhs.getY() < rhs.getY() ) ? 1 : 0;
			}
		});
		for (Sprite sprite : temp) {
			sprite.draw(mapRenderer.getBatch());
		}

		TiledMapTileLayer ceilMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.CEILING_LAYER);
		if( ceilMapLayer != null){
			mapRenderer.renderTileLayer(ceilMapLayer);
		}

		mapRenderer.getBatch().end();
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
