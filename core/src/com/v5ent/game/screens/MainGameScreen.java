package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.v5ent.game.Inspiration;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.core.WorldRenderer;
import com.v5ent.game.utils.Assets;


public class MainGameScreen implements Screen{

	private static final String TAG = MainGameScreen.class.getSimpleName();

		Inspiration game; // Note it's "MyGame" not "Game"
		private WorldController worldController;
		private WorldRenderer worldRenderer;
		private boolean paused;
		public MainGameScreen(Inspiration game) {
			this.game = game;
		}

	//make input.setInputProcessor(multiplexer);
	public void resetGame(){
		// Initialize controller and renderer
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);
		worldController.resetGame();
	}
			@Override
		public void show () {
				// Initialize controller and renderer
				worldController = new WorldController();
				worldRenderer = new WorldRenderer(worldController);
//			setGameState(GameState.LOADING);
		}

		@Override
		public void render (float delta) {
			if( worldController.isGameOver() ){
//				_mapMgr.disableCurrentmapMusic();
				game.setScreen(game.gameOverScreen);
			}

		/*	if( _gameState == GameState.PAUSED ){
				_player.updateInput(delta);
				_playerHUD.render(delta);
				return;
			}*/
			// Do not update game world when paused.
			if (!paused) {
				// Update game world by the time that has passed
				// since last rendered frame.
				worldController.update(delta);
			}
			// Render game world to screen
			worldRenderer.render(delta);
		}

		@Override
		public void resize (int width, int height) {
			worldRenderer.resize(width, height);
		}

		@Override
		public void pause () {
			paused = true;
		}

		@Override
		public void resume () {
			Assets.instance.init(new AssetManager());
			paused = false;
		}

		@Override
		public void dispose () {
			if(worldRenderer!=null){
				worldRenderer.dispose();
			}
		}

		@Override
		public void hide() {
			Gdx.input.setInputProcessor(null);
		}

}
