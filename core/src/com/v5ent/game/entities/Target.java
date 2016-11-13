package com.v5ent.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.v5ent.game.utils.AssetsManager;

/**
 * Created by Mignet on 2016/11/13.
 */

public class Target extends Sprite {
    private float frameTime = 0f;
    private Animation touchPointAnimation;
    private TextureRegion currentFrame = null;

    public Target(int x,int y) {
        this.touchPointAnimation = AssetsManager.instance.touch.touchPointAnimation;
        currentFrame =touchPointAnimation.getKeyFrame(0);
        // Define sprite size to be 1m x 1m in game world
        this.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        // Set origin to sprite's center
        this.setOrigin(this.getWidth() / 2.0f, 0);
        this.setPosition(x*32f,y*32f);
    }

    public void update(float delta) {
        frameTime = (frameTime + delta) % 4;
        currentFrame = touchPointAnimation.getKeyFrame(frameTime);
    }

    @Override
    public void draw(Batch batch) {
        // Draw image
        batch.draw(currentFrame.getTexture(), getX(), getY(),getOriginX(), getOriginY(), getWidth(),getHeight(), getScaleX(), getScaleY(),
                getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
//			Gdx.app.debug(TAG, "hero's coor:"+getX()+","+getY());
        // Reset color to white
        batch.setColor(1, 1, 1, 1);
    }
}
