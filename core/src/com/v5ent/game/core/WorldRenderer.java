package com.v5ent.game.core;

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

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;

	public WorldRenderer (WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init () {
		batch = new SpriteBatch();
		camera = worldController.camera;
	}

	public void render () {
		renderMap();
		renderTestObjects();
	}

	private void renderMap(){
		//Preferable to lock and center the camera to the player's position
		camera.position.set(worldController.player.getX(), worldController.player.getY(), 0f);
		camera.update();

		//updatePortalLayerActivation(player.boundingBox);
		//Collision Test
		/*if( !isCollisionWithMapLayer(player.getNextPosition()) ){
			player.setNextPositionToCurrent();
		}*/

		//_mapRenderer.getBatch().enableBlending();
		//_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		worldController._mapRenderer.setView(camera);
//		_mapRenderer.render();
		worldController._mapRenderer.getBatch().begin();
		//地面
		TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.GROUD_LAYER);
		if( groundMapLayer != null){
			worldController._mapRenderer.renderTileLayer(groundMapLayer);
		}
		TiledMapTileLayer floorMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.FLOOR_LAYER);
		if( floorMapLayer != null){
			worldController._mapRenderer.renderTileLayer(floorMapLayer);
		}
		//障碍物
		TiledMapTileLayer blockMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.BLOCK_LAYER);
		if( blockMapLayer != null ){
			worldController._mapRenderer.renderTileLayer(blockMapLayer);
		}

		worldController._mapRenderer.getBatch().end();

		worldController._mapRenderer.getBatch().begin();

//		worldController._mapRenderer.getBatch().draw(currentPlayerFrame, _currentPlayerSprite.getX()-currentPlayerFrame.getRegionWidth()/2, _currentPlayerSprite.getY(), currentPlayerFrame.getRegionWidth(),currentPlayerFrame.getRegionHeight());
		worldController.player.draw(worldController._mapRenderer.getBatch());

		TiledMapTileLayer ceilMapLayer = (TiledMapTileLayer)worldController.mapMgr.getCurrentMap().getLayers().get(MapsManager.CEILING_LAYER);
		if( ceilMapLayer != null){
			worldController._mapRenderer.renderTileLayer(ceilMapLayer);
		}

		worldController._mapRenderer.getBatch().end();
	}

	private void renderTestObjects () {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.player.draw(batch);
		batch.end();
	}

	public void resize (int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		camera.update();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	private boolean isCollisionWithMapLayer(Vector2 playerNextPos){
		TiledMapTileLayer mapCollisionLayer =  (TiledMapTileLayer)worldController.mapMgr.getPortalLayer();

		if( mapCollisionLayer == null ){
			return false;
		}

		int x = MathUtils.floor(playerNextPos.x / MapsManager.CELL_UNIT);
		int y = MathUtils.floor(playerNextPos.y / MapsManager.CELL_UNIT);
//		Gdx.app.debug(TAG, "Player Next (" + x + "," + y + ")");
		if (mapCollisionLayer.getCell(x, y) != null) {
			Gdx.app.debug(TAG, "CMap Collision!");
			return true;
		}

		return false;
	}

	/***
	 *
	 * @param boundingBox
	 * @return
     */
	private boolean updatePortalLayerActivation(Rectangle boundingBox){
		MapLayer mapPortalLayer =  worldController.mapMgr.getPortalLayer();

		if( mapPortalLayer == null ){
			return false;
		}

		Rectangle rectangle = null;

		for( MapObject object: mapPortalLayer.getObjects()){
			if(object instanceof RectangleMapObject) {
				rectangle = ((RectangleMapObject)object).getRectangle();
				//Gdx.app.debug(TAG, "Collision Rect (" + rectangle.x + "," + rectangle.y + ")");
				//Gdx.app.debug(TAG, "Player Rect (" + boundingBox.x + "," + boundingBox.y + ")");
				if( boundingBox.overlaps(rectangle) ){
					String mapName = object.getName();
					if( mapName == null ) {
						return false;
					}

//					mapMgr.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
					worldController.mapMgr.loadMap(mapName);
					//地图传送点
//					player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);
					worldController._mapRenderer.setMap(worldController.mapMgr.getCurrentMap());
					Gdx.app.debug(TAG, "Portal Activated");
					return true;
				}
			}
		}

		return false;
	}
}
