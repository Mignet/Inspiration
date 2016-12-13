package com.v5ent.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.v5ent.game.screens.GameOverScreen;
import com.v5ent.game.utils.Assets;
import com.v5ent.game.screens.MainGameScreen;

public class Inspiration extends Game {
	public  MainGameScreen mainGameScreen = null;
	public GameOverScreen gameOverScreen = null;


	@Override
	public void create() {
		//资源先加载
		Assets.instance.init(new AssetManager());
		mainGameScreen = new MainGameScreen(this);
		gameOverScreen = new GameOverScreen(this);
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		Assets.instance.dispose();
		mainGameScreen.dispose();
		gameOverScreen.dispose();
	}
}
