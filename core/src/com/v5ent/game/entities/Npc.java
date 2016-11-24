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

    Random r = new Random(10);

    public Npc(String entityId) {
        super(entityId);
        this.setCurrentDir(Direction.UP);
        this.setState(State.IDLE);
    }

    public void randomMove(WorldController worldController){
        int x = MathUtils.floor(getX()/32);
        int y = MathUtils.floor(getY()/32);
        if(this.getState()==State.IDLE){
            int randInt = r.nextInt(32);
            if(randInt<4){
                switch (randInt){
                    case 0:
                        setCurrentDir(Direction.RIGHT);
                        moveTo(x+randInt, y);
                        break;
                    case 1:
                        setCurrentDir(Direction.LEFT);
                        moveTo(x-randInt, y);
                        break;
                    case 2:
                        setCurrentDir(Direction.UP);
                        moveTo(x, y+randInt);
                        break;
                    case 3:
                        setCurrentDir(Direction.DOWN);
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
//                setPosInMap(new Vector2(x,y));

                setState(State.IDLE);
            }
        }
    }
}
