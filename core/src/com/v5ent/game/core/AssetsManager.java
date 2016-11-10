package com.v5ent.game.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class AssetsManager implements Disposable, AssetErrorListener {

	public static final String TAG = AssetsManager.class.getName();

	public static final AssetsManager instance = new AssetsManager();

	private AssetManager assetManager;
	/** load all characters */
	public Map<String,AssetRole> assetRoles = new HashMap<String, AssetRole>();
	/** load all tiled map */
	public Map<String,AssetTiledMap> assetTiledMaps = new HashMap<String, AssetTiledMap>();
	// singleton: prevent instantiation from other classes
	private AssetsManager() {
	}

	public class AssetRole {
		public final Animation walkDownAnimation;
		public final Animation walkLeftAnimation;
		public final Animation walkRightAnimation;
		public final Animation walkUpAnimation;

		public AssetRole(Texture atlas) {
			TextureRegion[][] textureFrames = TextureRegion.split(atlas, 32, 48);
			Array<TextureRegion> walkDownFrames = new Array<TextureRegion>(3);
			Array<TextureRegion> walkLeftFrames = new Array<TextureRegion>(3);
			Array<TextureRegion> walkRightFrames = new Array<TextureRegion>(3);
			Array<TextureRegion> walkUpFrames = new Array<TextureRegion>(3);

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++) {
					TextureRegion region = textureFrames[i][j];
					if( region == null ){
						Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
					}
					switch(i)
					{
						case 0:
							walkDownFrames.insert(j, region);
							break;
						case 1:
							walkLeftFrames.insert(j, region);
							break;
						case 2:
							walkUpFrames.insert(j, region);
							break;
						case 3:
							walkRightFrames.insert(j, region);
							break;
					}
				}
			}
			walkDownAnimation = new Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
			walkLeftAnimation = new Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
			walkRightAnimation = new Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
			walkUpAnimation = new Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
		}
	}

	public class AssetTiledMap{
		public final TiledMap tiledMap;
		public final String mapName;
		public final int mapTileWidth;
		public final int mapTileHeight;
		public final int tileWidth;
		public final int tileHeight;
		public final int mapPixelWidth;
		public final int mapPixelHeight;
		public AssetTiledMap(TiledMap currentTileMap){
			tiledMap = currentTileMap;
			MapProperties props =  currentTileMap.getProperties();
			mapName = props.get("mapName",String.class);
			mapTileWidth = props.get("width",Integer.class);
			mapTileHeight = props.get("height",Integer.class);
			tileWidth = props.get("tilewidth",Integer.class);
			tileHeight = props.get("tileheight",Integer.class);
			mapPixelWidth = mapTileWidth * tileWidth;
			mapPixelHeight = mapTileHeight * tileHeight;
		}
	}

	public void init (AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		this.assetManager.setErrorListener(this);
		this.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		// load texture atlas
		//TODO:当前提供的所有角色
		int heroCnt = 2;

		//look all hero's pack
		for(int i=1;i<=heroCnt ;i++){
			assetManager.load("heros/00"+i+".png", Texture.class);
		}
		int mapCnt = 1;
		//look all map's file
		for(int i=1;i<=mapCnt ;i++){
			assetManager.load("maps/00"+i+".tmx", TiledMap.class);
		}
		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}
		//store into map
		for(int i=1;i<=heroCnt ;i++){
			Texture atlas = assetManager.get("heros/00"+i+".png");
			// create game resource objects
			assetRoles.put("00"+i,new AssetRole(atlas));
		}
		for(int i=1;i<=mapCnt ;i++){
			TiledMap map = assetManager.get("maps/00"+i+".tmx");
			// create game resource objects
			assetTiledMaps.put("00"+i,new AssetTiledMap(map));
		}
	}

	@Override
	public void dispose () {
		assetManager.dispose();
	}

	@Override
	public void error(AssetDescriptor assetDesc, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + assetDesc.fileName + "'", throwable);
	}

}
