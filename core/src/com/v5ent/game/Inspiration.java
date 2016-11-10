package com.v5ent.game;

import com.badlogic.gdx.Game;
import com.v5ent.game.screens.MainGameScreen;

public class Inspiration extends Game {
	public  MainGameScreen mainGameScreen = null;

	@Override
	public void create() {
		mainGameScreen = new MainGameScreen(this);
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();
	}
}
