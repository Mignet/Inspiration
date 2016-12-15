package com.v5ent.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.utils.Assets;

/**
 * Created by Mignet on 2016/11/13.
 */

public class Enemy extends Role {
    private static final String TAG = Enemy.class.getSimpleName();
    private final Texture hpBar;
    private final Texture bar;

    private State defaultState;

    private Direction defaultDir;

    public void setDefaultDir(Direction defaultDir) {
        this.defaultDir = defaultDir;
    }

    public Direction getDefaultDir() {
        return defaultDir;
    }

    public State getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(State defaultState) {
        this.defaultState = defaultState;
    }

    public Enemy(String entityId) {
        super(entityId);
        this.setCurrentDir(Direction.UP);
        this.setState(State.IDLE);
        hpBar = Assets.instance.hpBar;
        bar = Assets.instance.bar;
    }

    public void randomMove(WorldController worldController){
        if(this.getState()==State.FIXED)return;
        int x = MathUtils.floor(getX()/32);
        int y = MathUtils.floor(getY()/32);
        if(this.getState()==State.IDLE){
            int randInt = MathUtils.random(128);
            Gdx.app.debug(TAG,"randInt:"+randInt);
            if(randInt<4){
                switch (randInt){
                    case 0:
                        moveTo(x+randInt, y);
                        break;
                    case 1:
                        moveTo(x-randInt, y);
                        break;
                    case 2:
                        moveTo(x, y+randInt);
                        break;
                    case 3:
                        moveTo(x, y-randInt);
                        break;
                }
            }else{
                setState(State.IDLE);
            }
        }
        if(this.getState()==State.WALKING){
            if((getCurrentDir()==Direction.RIGHT && (worldController.isCollisionWithBlock(x+1,y)||worldController.isCollisionWithPlayer(x+1,y)))
                    ||(getCurrentDir()==Direction.LEFT && (worldController.isCollisionWithBlock(x-1,y)||worldController.isCollisionWithPlayer(x-1,y)))
                    ||(getCurrentDir()==Direction.UP && (worldController.isCollisionWithBlock(x,y+1)||worldController.isCollisionWithPlayer(x,y+1)))
                    ||(getCurrentDir()==Direction.DOWN && (worldController.isCollisionWithBlock(x,y-1)||worldController.isCollisionWithPlayer(x,y-1)))
                    ){
                setState(State.IDLE);
            }
        }
    }
    @Override
    public void draw(Batch batch) {

        batch.draw(hpBar,getX(),getHeight()+getY()-2,32*getHealthPoint()/100,hpBar.getHeight());
        batch.draw(bar,getX(),getHeight()+getY()-2,32,bar.getHeight());
        batch.draw(Assets.instance.shadow,getX(),getY()-2);
        if(getState()==State.ATTACK){
            // Draw image
            batch.draw(currentFrame.getTexture(), getX()-32, getY(),getOriginX(), getOriginY(), getWidth()*2,getHeight(), getScaleX(), getScaleY(),
                    getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
        }else{
            // Draw image
            batch.draw(currentFrame.getTexture(), getX(), getY(),getOriginX(), getOriginY(), getWidth(),getHeight(), getScaleX(), getScaleY(),
                    getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
        }
        // Reset color to white
        batch.setColor(1, 1, 1, 1);
    }
}
