package com.v5ent.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.v5ent.game.utils.Assets;
import com.v5ent.game.screens.MainGameScreen;

public class Inspiration extends Game {
	public  MainGameScreen mainGameScreen = null;


	@Override
	public void create() {
		//资源先加载
		Assets.instance.init(new AssetManager());
		mainGameScreen = new MainGameScreen(this);
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		Assets.instance.dispose();
		mainGameScreen.dispose();
	}
}
