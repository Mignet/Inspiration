/**
 * Project Name:arpg-core
 * File Name:Skill.java
 * Package Name:com.v5ent.game.entitys.skill
 * Date:2015-8-2下午11:10:10
 * Copyright (c) 2015, V5Games All Rights Reserved.
 *
*/

package com.v5ent.game.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 技能基类 <br/>
 * @author   Mignet
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public abstract class Skill extends Sprite {

	private static final String TAG = Skill.class.getName();
	/** 技能伤害 */
	private double damage = 0;
	
	public Skill(){
		Gdx.app.debug(TAG, "---init Skill ---");
	}
	public void update(float deltaTime) {
	}

	public abstract boolean isEnded();
}

