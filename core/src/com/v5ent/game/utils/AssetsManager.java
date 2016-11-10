package com.v5ent.game.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
	public Map<String,AssetHero> assetHeros = new HashMap<String, AssetHero>();
	/** load all tiled map */
	public Map<String,AssetTiledMap> assetTiledMaps = new HashMap<String, AssetTiledMap>();
	// singleton: prevent instantiation from other classes
	private AssetsManager() {
	}

	public class AssetHero {
		public final Animation idleRightAnimation;
		public final Animation walkRightAnimation;

		public AssetHero (TextureAtlas atlas) {
			Array<TextureRegion> idleRightFrames = new Array<TextureRegion>(4);
			for (int i = 0; i < 3; i++) {
				idleRightFrames.insert(i, atlas.findRegion("idleRight"+i));
			}
			idleRightAnimation = new Animation(0.25f, idleRightFrames, Animation.PlayMode.LOOP);
			Array<TextureRegion> walkRightFrames = new Array<TextureRegion>(4);
			for (int i = 0; i < 3; i++) {
				walkRightFrames.insert(i, atlas.findRegion("walkRight"+i));
			}
			walkRightAnimation = new Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
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
			assetManager.load("heros/00"+i+".pack", TextureAtlas.class);
		}
		int mapCnt = 2;
		//look all map's file
		for(int i=1;i<=mapCnt ;i++){
			assetManager.load("map/00"+i+".pack", TiledMap.class);
		}
		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}
		//store into map
		for(int i=1;i<=heroCnt ;i++){
			TextureAtlas atlas = assetManager.get("heros/00"+i+".pack");
			// create game resource objects
			assetHeros.put("00"+i,new AssetHero(atlas));
		}
		for(int i=1;i<=mapCnt ;i++){
			TiledMap map = assetManager.get("maps/00"+i+".pack");
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
