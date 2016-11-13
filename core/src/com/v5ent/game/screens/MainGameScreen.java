package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.v5ent.game.Inspiration;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.core.WorldRenderer;
import com.v5ent.game.utils.AssetsManager;

public class MainGameScreen implements Screen{

	private static final String TAG = MainGameScreen.class.getSimpleName();

		Inspiration game; // Note it's "MyGame" not "Game"
		private WorldController worldController;
		private WorldRenderer worldRenderer;

		private boolean paused;

		public MainGameScreen(Inspiration game) {
			this.game = game;
		}

		@Override
		public void show () {
			// Initialize controller and renderer
			worldController = new WorldController();
			worldRenderer = new WorldRenderer(worldController);
		}

		@Override
		public void render (float delta) {
			// Do not update game world when paused.
			if (!paused) {
				// Update game world by the time that has passed
				// since last rendered frame.
				worldController.update(delta);
			}
			// Sets the clear screen color to: Cornflower Blue
			Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
			// Clears the screen
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			// Render game world to screen
			worldRenderer.render();
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
			AssetsManager.instance.init(new AssetManager());
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
			// TODO Auto-generated method stub

		}

}
