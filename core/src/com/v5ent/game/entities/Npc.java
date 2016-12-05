package com.v5ent.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.core.WorldController;

import java.util.Random;

/**
 * Created by Mignet on 2016/11/13.
 */

public class Npc extends Role {
    private static final String TAG = Npc.class.getSimpleName();

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
    public Npc(String entityId) {
        super(entityId);
        this.setCurrentDir(Direction.UP);
        this.setState(State.IDLE);
    }

    public void randomMove(WorldController worldController){
        if(this.getState()==State.FIXED)return;
        int x = MathUtils.floor(getX()/32);
        int y = MathUtils.floor(getY()/32);
        if(this.getState()==State.IDLE){
            Random r = new Random(System.currentTimeMillis());
            int randInt = r.nextInt(64);
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
}
