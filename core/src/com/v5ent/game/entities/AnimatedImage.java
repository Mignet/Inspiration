package com.v5ent.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class AnimatedImage extends Image {
    private static final String TAG = AnimatedImage.class.getSimpleName();
    private float _frameTime = 0;
    protected Role _entity;
    private Role.State _currentAnimationType = Role.State.IDLE;

    public AnimatedImage(){
        super();
    }

    public void setEntity(Role entity){
        this._entity = entity;
        //set default
        setCurrentAnimation(Role.State.IDLE);
    }

    public void setCurrentAnimation(Role.State animationType){
        Animation animation = _entity.getAnimation(animationType);
        if( animation == null ){
            Gdx.app.debug(TAG, "Animation type " + animationType.toString() + " does not exist!");
            return;
        }

        this._currentAnimationType = animationType;
        this.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(0)));
        this.setScaling(Scaling.stretch);
        this.setAlign(Align.center);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    @Override
    public void act(float delta){
        Drawable drawable = this.getDrawable();
        if( drawable == null ) {
            //Gdx.app.debug(TAG, "Drawable is NULL!");
            return;
        }
        _frameTime = (_frameTime + delta)%5;
        TextureRegion region = _entity.getAnimation(_currentAnimationType).getKeyFrame(_frameTime, true);
        //Gdx.app.debug(TAG, "Keyframe number is " + _animation.getKeyFrameIndex(_frameTime));
        ((TextureRegionDrawable) drawable).setRegion(region);
        super.act(delta);
    }

}
