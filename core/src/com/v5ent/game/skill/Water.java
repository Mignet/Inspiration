/**
 * Project Name:arpg-core
 * File Name:Water.java
 * Package Name:com.v5ent.game.entitys.skill
 * Date:2015-8-2下午11:10:10
 * Copyright (c) 2015, V5Games All Rights Reserved.
 *
*/

package com.v5ent.game.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.v5ent.game.utils.Assets;

/**
 * ClassName:Water <br/>
 * Function: Skill Water<br/>
 * @author   Mignet
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class Water extends Skill {

private static final String TAG = Water.class.getName();
	
	private static final int FRAMES = 35;
	Animation curAnimation;
	private TextureRegion[] walkFrames;
	private TextureRegion currentFrame;
	
	public Water(){
		init();
	}
	float stateTime;
	int width,height;
	
	private void init() {
		Gdx.app.debug(TAG, "---init Water curAnimation by walkFrames---");
		walkFrames = new TextureRegion[FRAMES];
		for (int i = 0; i < FRAMES; i++) {
			walkFrames[i] = Assets.water[i];
		}
		curAnimation = new Animation(0.06f, walkFrames);
		stateTime = 0;
		currentFrame = curAnimation.getKeyFrame(stateTime, true);
		width = currentFrame.getRegionWidth();
		height = currentFrame.getRegionHeight();
	}
	
	public void update(float deltaTime){
		stateTime += deltaTime;
		currentFrame = curAnimation.getKeyFrame(stateTime, false);
	}

	@Override
	public void draw(Batch batch) {
		batch.draw(currentFrame,this.getX()-200, this.getY()-100);
	}
	
	public boolean isEnded(){
		return stateTime>=FRAMES*0.06;
	}

}

