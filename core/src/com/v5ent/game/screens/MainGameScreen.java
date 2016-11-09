package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.entities.Player;

public class MainGameScreen implements Screen{

	private static final String TAG = MainGameScreen.class.getSimpleName();

	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	private PlayerController _controller;
	private TextureRegion currentPlayerFrame;
	private Sprite _currentPlayerSprite;

	private OrthogonalTiledMapRenderer _mapRenderer = null;
	private OrthographicCamera camera = null;
	private static MapManager mapMgr;

	public MainGameScreen(){
		mapMgr = new MapManager();
	}

	private static Player player;

	@Override
	public void show() {
		//camera setup
		setupViewport(640, 480);

		//get the current size
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		_mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentMap());
		_mapRenderer.setView(camera);

		Gdx.app.debug(TAG, "UnitScale value is: " + _mapRenderer.getUnitScale());

		player = new Player();
		player.setPos(mapMgr.getStartPos());

		_currentPlayerSprite = player.getFrameSprite();

		_controller = new PlayerController(player);
		Gdx.input.setInputProcessor(_controller);
	}

	@Override
	public void hide() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Preferable to lock and center the camera to the player's position
		camera.position.set(_currentPlayerSprite.getX(), _currentPlayerSprite.getY(), 0f);
		camera.update();

		player.update(delta);
		currentPlayerFrame = player.getFrame();

		updatePortalLayerActivation(player.boundingBox);
		//Collision Test
		if( !isCollisionWithMapLayer(player.getNextPosition()) ){
			player.setNextPositionToCurrent();
		}
		_controller.update(delta);

		//_mapRenderer.getBatch().enableBlending();
		//_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		_mapRenderer.setView(camera);
//		_mapRenderer.render();
		_mapRenderer.getBatch().begin();
		//地面
		TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)mapMgr.getCurrentMap().getLayers().get(Map.GROUD_LAYER);
		if( groundMapLayer != null){
			_mapRenderer.renderTileLayer(groundMapLayer);
		}
		TiledMapTileLayer floorMapLayer = (TiledMapTileLayer)mapMgr.getCurrentMap().getLayers().get(Map.FLOOR_LAYER);
		if( floorMapLayer != null){
			_mapRenderer.renderTileLayer(floorMapLayer);
		}
		//障碍物
		TiledMapTileLayer blockMapLayer = (TiledMapTileLayer)mapMgr.getCurrentMap().getLayers().get(Map.BLOCK_LAYER);
		if( blockMapLayer != null ){
			_mapRenderer.renderTileLayer(blockMapLayer);
		}
		
		_mapRenderer.getBatch().end();

		_mapRenderer.getBatch().begin();
		
		_mapRenderer.getBatch().draw(currentPlayerFrame, _currentPlayerSprite.getX()-currentPlayerFrame.getRegionWidth()/2, _currentPlayerSprite.getY(), currentPlayerFrame.getRegionWidth(),currentPlayerFrame.getRegionHeight());
		
		TiledMapTileLayer ceilMapLayer = (TiledMapTileLayer)mapMgr.getCurrentMap().getLayers().get(Map.CEILING_LAYER);
		if( ceilMapLayer != null){
			_mapRenderer.renderTileLayer(ceilMapLayer);
		}
		
		_mapRenderer.getBatch().end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		player.dispose();
		_controller.dispose();
		Gdx.input.setInputProcessor(null);
		_mapRenderer.dispose();
	}

	/**
	 * setup the virtual world
	 * @param width
	 * @param height
     */
	private void setupViewport(int width, int height){
		//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//update viewport if there could be skewing
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			//letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}

	private boolean isCollisionWithMapLayer(Vector2 playerNextPos){
		TiledMapTileLayer mapCollisionLayer =  (TiledMapTileLayer)mapMgr.getCollisionLayer();

		if( mapCollisionLayer == null ){
			return false;
		}

		int x = MathUtils.floor(playerNextPos.x / Map.CELL_UNIT);
		int y = MathUtils.floor(playerNextPos.y / Map.CELL_UNIT);
//		Gdx.app.debug(TAG, "Player Next (" + x + "," + y + ")");
		if (mapCollisionLayer.getCell(x, y) != null) {
			 Gdx.app.debug(TAG, "Map Collision!");
			return true;
		}

		return false;
	}

	private boolean updatePortalLayerActivation(Rectangle boundingBox){
		MapLayer mapPortalLayer =  mapMgr.getPortalLayer();

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
					mapMgr.loadMap(mapName);
					//地图传送点
//					player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);
					_mapRenderer.setMap(mapMgr.getCurrentMap());
					Gdx.app.debug(TAG, "Portal Activated");
					return true;
				}
			}
		}

		return false;
	}

}
