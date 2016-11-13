package com.v5ent.game.entities;

/**
 * Created by Mignet on 2016/11/13.
 */

public class Npc extends Role {
    private static final String TAG = Npc.class.getSimpleName();

    public Npc(String entityId) {
        super(entityId);
        this.setCurrentDir(Direction.UP);
        this.setState(State.IDLE);
    }
}
