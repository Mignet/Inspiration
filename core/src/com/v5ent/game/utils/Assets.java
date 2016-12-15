package com.v5ent.game.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import static com.v5ent.game.utils.Resource.ITEMS_TEXTURE_ATLAS_PATH;
import static com.v5ent.game.utils.Resource.STATUSUI_SKIN_PATH;
import static com.v5ent.game.utils.Resource.STATUSUI_TEXTURE_ATLAS_PATH;

public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName();

	public static final Assets instance = new Assets();
	/** libGDX 's asset manager*/
	private static AssetManager assetManager;
	private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();
	/** load all characters */
	public Map<String,AssetRole> assetRoles = new HashMap<String, AssetRole>();
	/** load all tiled map */
	public Map<String,AssetTiledMap> assetTiledMaps = new HashMap<String, AssetTiledMap>();
	public AssetTouch touch;
	public Texture shadow;
	public Texture selected;
	public Texture hpBar,bar;

	public static TextureRegion[] water;
	public static TextureRegion[] earth;
	public static TextureRegion[] fire;
	public static TextureRegion[] thunder;

	public TextureAtlas STATUSUI_TEXTUREATLAS ;
	public TextureAtlas ITEMS_TEXTUREATLAS ;
	public Skin STATUSUI_SKIN ;
	public Texture MONSTERS0;
	public Texture MONSTERS1;
	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public class AssetRole {
		public final Animation idleDownAnimation;
		public final Animation idleLeftAnimation;
		public final Animation idleRightAnimation;
		public final Animation idleUpAnimation;
		public final Animation walkDownAnimation;
		public final Animation walkLeftAnimation;
		public final Animation walkRightAnimation;
		public final Animation walkUpAnimation;
		public  Animation attackDownAnimation;
		public  Animation attackLeftAnimation;
		public  Animation attackRightAnimation;
		public  Animation attackUpAnimation;

		public AssetRole(Texture atlas,Texture attack) {
			//role's animation png is 0-idle,1-walk,2-walk
			TextureRegion[][] textureFrames = TextureRegion.split(atlas, 32, 48);
			Array<TextureRegion> idleDownFrames = new Array<TextureRegion>(1);
			Array<TextureRegion> idleLeftFrames = new Array<TextureRegion>(1);
			Array<TextureRegion> idleRightFrames = new Array<TextureRegion>(1);
			Array<TextureRegion> idleUpFrames = new Array<TextureRegion>(1);
			Array<TextureRegion> walkDownFrames = new Array<TextureRegion>(2);
			Array<TextureRegion> walkLeftFrames = new Array<TextureRegion>(2);
			Array<TextureRegion> walkRightFrames = new Array<TextureRegion>(2);
			Array<TextureRegion> walkUpFrames = new Array<TextureRegion>(2);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++) {
					TextureRegion region = textureFrames[i][j];
					if( region == null ){
						Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
					}
					if(j==0){
						switch(i)
						{
							case 0:
								idleDownFrames.insert(0, region);
								break;
							case 1:
								idleLeftFrames.insert(0, region);
								break;
							case 2:
								idleUpFrames.insert(0, region);
								break;
							case 3:
								idleRightFrames.insert(0, region);
								break;
						}
					}else{
						switch(i)
						{
							case 0:
								walkDownFrames.insert(j-1, region);
								break;
							case 1:
								walkLeftFrames.insert(j-1, region);
								break;
							case 2:
								walkUpFrames.insert(j-1, region);
								break;
							case 3:
								walkRightFrames.insert(j-1, region);
								break;
						}
					}
				}
			}
			idleDownAnimation = new Animation(0.25f, idleDownFrames, Animation.PlayMode.LOOP);
			idleLeftAnimation = new Animation(0.25f, idleLeftFrames, Animation.PlayMode.LOOP);
			idleRightAnimation = new Animation(0.25f, idleRightFrames, Animation.PlayMode.LOOP);
			idleUpAnimation = new Animation(0.25f, idleUpFrames, Animation.PlayMode.LOOP);
			walkDownAnimation = new Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
			walkLeftAnimation = new Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
			walkRightAnimation = new Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
			walkUpAnimation = new Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
			attackDownAnimation = null;
			attackLeftAnimation = null;
			attackRightAnimation = null;
			attackUpAnimation = null;
			if(attack!=null){
				TextureRegion[][] attackFrames = TextureRegion.split(attack, 64, 48);
				Array<TextureRegion> attackDownFrames = new Array<TextureRegion>(3);
				Array<TextureRegion> attackLeftFrames = new Array<TextureRegion>(3);
				Array<TextureRegion> attackRightFrames = new Array<TextureRegion>(3);
				Array<TextureRegion> attackUpFrames = new Array<TextureRegion>(3);
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 3; j++) {
						TextureRegion region = attackFrames[i][j];
						if (region == null) {
							Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
						}
						switch (i) {
							case 0:
								attackDownFrames.insert(j, region);
								break;
							case 1:
								attackLeftFrames.insert(j, region);
								break;
							case 2:
								attackUpFrames.insert(j, region);
								break;
							case 3:
								attackRightFrames.insert(j, region);
								break;
						}
					}
				}
				attackDownAnimation = new Animation(0.25f, attackDownFrames, Animation.PlayMode.LOOP);
				attackLeftAnimation = new Animation(0.25f, attackLeftFrames, Animation.PlayMode.LOOP);
				attackRightAnimation = new Animation(0.25f, attackRightFrames, Animation.PlayMode.LOOP);
				attackUpAnimation = new Animation(0.25f, attackUpFrames, Animation.PlayMode.LOOP);
			}
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

	public class AssetTouch{
		public final Animation touchPointAnimation;
		public AssetTouch(Texture atlas){
			TextureRegion[][] textureFrames = TextureRegion.split(atlas, 32, 32);
			Array<TextureRegion> touchPointFrames = new Array<TextureRegion>(3);
			for( int i=0;i<2;i++){
				touchPointFrames.insert(i,textureFrames[0][i]);
			}
			touchPointAnimation = new Animation(0.25f, touchPointFrames, Animation.PlayMode.LOOP);
		}
	}
	private static TextureRegion[] loads(String pre, String tail, int mnt,
										 int width, int height) {
		TextureRegion[] res = new TextureRegion[mnt];
		for(int i=0;i<mnt;i++){
			res[i] = load(pre+(i+1)+tail,width,height);
		}
		return res;
	}
	/**
	 * 从文件加载Texture
	 *
	 * @param name
	 * @param width
	 * @param height
	 * @return
	 */
	public static TextureRegion load (String name, int width, int height) {
		Texture texture = new Texture(Gdx.files.internal(name));
		TextureRegion region = new TextureRegion(texture, 0, 0, width, height);
//		region.flip(false, true);
		return region;
	}

	public void init (AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		this.assetManager.setErrorListener(this);
		this.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		// load texture atlas
		water = loads("skill/storm/storm (",").png",35, 495, 331);
		earth = loads("skill/sand/sand (",").png",26, 428, 369);
		fire = loads("skill/fire/fir (",").png",28, 464, 273);
		thunder = loads("skill/thunder/benlei (",").png",35, 450, 450);
		//load all hero's pack
		for (Map.Entry<String, String> entry : Resource.instance.players.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			assetManager.load(value, Texture.class);
			String attackFilePath = Resource.instance.fighters.get(key);
			if(attackFilePath!=null){
				assetManager.load(attackFilePath, Texture.class);
			}
		}
		for (Map.Entry<String, String> entry : Resource.instance.npcs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			assetManager.load(value, Texture.class);
		}
		//TODO:load all map's file
		for (Map.Entry<String, String> entry : Resource.instance.maps.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			assetManager.load(value, TiledMap.class);
		}
		assetManager.load(Resource.MONSTERS0,Texture.class);
		assetManager.load(Resource.MONSTERS1,Texture.class);
		assetManager.load(Resource.TOUCH,Texture.class);
		assetManager.load(Resource.SHADOW,Texture.class);
		assetManager.load(Resource.SELECTED,Texture.class);
		assetManager.load(Resource.HP_BAR,Texture.class);
		assetManager.load(Resource.BAR,Texture.class);

		assetManager.load(Resource.STATUSUI_TEXTURE_ATLAS_PATH,TextureAtlas.class);
		assetManager.load(Resource.ITEMS_TEXTURE_ATLAS_PATH,TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();

		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}
		//store into map
		for (Map.Entry<String, String> entry : Resource.instance.players.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			Texture atlas = assetManager.get(value);
			String attackFilePath = Resource.instance.fighters.get(key);
			Texture attackAtlas = null;
			if(attackFilePath!=null){
				attackAtlas = assetManager.get(attackFilePath);
			}
			// create game resource objects
			assetRoles.put(key,new AssetRole(atlas,attackAtlas));
		}
		for (Map.Entry<String, String> entry : Resource.instance.npcs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			Texture atlas = assetManager.get(value);
			// create game resource objects
			assetRoles.put(key,new AssetRole(atlas,null));
		}
		for (Map.Entry<String, String> entry : Resource.instance.maps.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			TiledMap map = assetManager.get(value);
			// create game resource objects
			assetTiledMaps.put(key,new AssetTiledMap(map));
		}
		touch = new AssetTouch(assetManager.get(Resource.TOUCH,Texture.class));
		shadow = assetManager.get(Resource.SHADOW,Texture.class);
		selected = assetManager.get(Resource.SELECTED,Texture.class);
		hpBar = assetManager.get(Resource.HP_BAR,Texture.class);
		bar = assetManager.get(Resource.BAR,Texture.class);
		STATUSUI_TEXTUREATLAS = assetManager.get(STATUSUI_TEXTURE_ATLAS_PATH,TextureAtlas.class);
		ITEMS_TEXTUREATLAS = assetManager.get(ITEMS_TEXTURE_ATLAS_PATH,TextureAtlas.class);
		STATUSUI_SKIN = new Skin(Gdx.files.internal(Resource.STATUSUI_SKIN_PATH), STATUSUI_TEXTUREATLAS);
		MONSTERS0 = assetManager.get(Resource.MONSTERS0,Texture.class);
		MONSTERS1 = assetManager.get(Resource.MONSTERS1,Texture.class);
	}
	public static void loadSoundAsset(String soundFilenamePath){
		if( soundFilenamePath == null || "".equals(soundFilenamePath.trim()) ){
			return;
		}
		if( assetManager.isLoaded(soundFilenamePath) ){
			return;
		}
		//load asset
		if( filePathResolver.resolve(soundFilenamePath).exists() ){
			assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
			assetManager.load(soundFilenamePath, Sound.class);
			//Until we add loading screen, just block until we load the map
			assetManager.finishLoadingAsset(soundFilenamePath);
			Gdx.app.debug(TAG, "Sound loaded!: " + soundFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Sound doesn't exist!: " + soundFilenamePath );
		}
	}


	public static Sound getSoundAsset(String soundFilenamePath){
		Sound sound = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(soundFilenamePath) ){
			sound = assetManager.get(soundFilenamePath,Sound.class);
		} else {
			Gdx.app.debug(TAG, "Sound is not loaded: " + soundFilenamePath );
		}

		return sound;
	}

	public static void loadMusicAsset(String musicFilenamePath){
		if( musicFilenamePath == null || "".equals(musicFilenamePath.trim()) ){
			return;
		}

		if( assetManager.isLoaded(musicFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(musicFilenamePath).exists() ){
			assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
			assetManager.load(musicFilenamePath, Music.class);
			//Until we add loading screen, just block until we load the map
			assetManager.finishLoadingAsset(musicFilenamePath);
			Gdx.app.debug(TAG, "Music loaded!: " + musicFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Music doesn't exist!: " + musicFilenamePath );
		}
	}


	public static Music getMusicAsset(String musicFilenamePath){
		Music music = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(musicFilenamePath) ){
			music = assetManager.get(musicFilenamePath,Music.class);
		} else {
			Gdx.app.debug(TAG, "Music is not loaded: " + musicFilenamePath );
		}

		return music;
	}
	public static boolean isAssetLoaded(String fileName){
		return assetManager.isLoaded(fileName);
	}

	@Override
	public void dispose () {
		assetManager.dispose();
	}

	@Override
	public void error(AssetDescriptor assetDesc, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + assetDesc.fileName + "'", throwable);
	}

	private Pixmap createProceduralPixmap(int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow-colored X shape on square
		pixmap.setColor(1, 1, 0, 1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		// Draw a cyan-colored border around square
		pixmap.setColor(0, 1, 1, 1);
		pixmap.fillRectangle(0, 0, width, height);
		return pixmap;
	}

}
