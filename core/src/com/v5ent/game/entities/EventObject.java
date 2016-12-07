package com.v5ent.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Mignet on 2016/12/7.
 */

public class EventObject extends Sprite {
    private String name;
    private String command;
    private boolean isToggled = false;
    private TextureRegion currentFrame = null;

    public EventObject(String name, TextureRegion textureRegion, float x, float y) {
        this.name = name;
        currentFrame = textureRegion;
        // Define sprite size to be 1m x 1m in game world
        this.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        // Set origin to sprite's center
//        this.setOrigin(this.getWidth() / 2.0f, 0);
        this.setPosition(x,y);
    }

    @Override
    public void draw(Batch batch) {
        if(isToggled){
            // Draw image
            batch.draw(currentFrame.getTexture(), getX(), getY(),getOriginX(), getOriginY(), getWidth(),getHeight(), getScaleX(), getScaleY(),
                    getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
            // Reset color to white
            batch.setColor(1, 1, 1, 1);
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isToggled() {
        return isToggled;
    }

    public void setToggled(boolean toggled) {
        isToggled = toggled;
    }
}
