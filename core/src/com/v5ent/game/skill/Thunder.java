/**
 * Project Name:arpg-core
 * File Name:Thunder.java
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
 * ClassName:Thunder <br/>
 * Function: Skill Thunder<br/>
 * @author   Mignet
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class Thunder extends Skill {

private static final String TAG = Thunder.class.getName();
	
	private static final int FRAMES = 35;
	Animation curAnimation;
	private TextureRegion[] walkFrames;
	private TextureRegion currentFrame;
	
	public Thunder(){
		init();
	}
	float stateTime;
	float frameDuration = 0.06f;

	private void init() {
		Gdx.app.debug(TAG, "---init Thunder curAnimation by walkFrames---");
		walkFrames = new TextureRegion[FRAMES];
		for (int i = 0; i < FRAMES; i++) {
			walkFrames[i] = Assets.thunder[i];
		}
		curAnimation = new Animation(frameDuration, walkFrames);
		stateTime = 0;
		currentFrame = curAnimation.getKeyFrame(stateTime, true);
	}
	
	public void update(float deltaTime){
		stateTime += deltaTime;
		currentFrame = curAnimation.getKeyFrame(stateTime, false);
	}

	@Override
	public void draw(Batch batch) {
		batch.draw(currentFrame,this.getX()-190, this.getY()-100);
	}
	
	public boolean isEnded(){
		return stateTime>=FRAMES*frameDuration;
	}

}

