package com.v5ent.game.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.utils.Assets;

import static com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShape.point;

/**
 * Created by Mignet on 2016/12/11.
 */

public class Monster extends Role {
    private static final String TAG = Monster.class.getSimpleName();

    public Monster(String entityId,int x,int y) {
        super(entityId);
        this.setCurrentDir(Direction.RIGHT);
        this.idleRightAnimation =loadAnimation(x,y);
        currentFrame =idleRightAnimation.getKeyFrame(0);
        // Define sprite size to be 1m x 1m in game world
//        this.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        // Set origin to sprite's center
        this.setOrigin(this.getWidth() / 2.0f, 0);
    }
    //Specific to two frame animations where each frame is stored in a separate texture
    protected Animation loadAnimation(int x,int y){

        TextureRegion[][] texture1Frames = TextureRegion.split(Assets.instance.MONSTERS0, 16, 16);
        TextureRegion[][] texture2Frames = TextureRegion.split(Assets.instance.MONSTERS1, 16, 16);

        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(2);

        animationKeyFrames.add(texture1Frames[x][y]);
        animationKeyFrames.add(texture2Frames[x][y]);

        return new Animation(0.5f, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    private int xpReward = 100;
    private int gpReward = 100;

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public int getGpReward() {
        return gpReward;
    }

    public void setGpReward(int gpReward) {
        this.gpReward = gpReward;
    }

}
