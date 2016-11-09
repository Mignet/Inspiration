package com.v5ent.game;

import com.badlogic.gdx.Game;
import com.v5ent.game.screens.MainGameScreen;

public class Inspiration extends Game {
	public static final MainGameScreen mainGameScreen = new MainGameScreen();

	@Override
	public void create() {
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();
	}
}
