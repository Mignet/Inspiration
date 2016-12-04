package com.v5ent.game.inventory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class InventorySlotTooltipListener extends InputListener {

    private InventorySlotTooltip toolTip;
    private boolean isInside = false;
    private Vector2 currentPos;
    private Vector2 offset;

    public InventorySlotTooltipListener(InventorySlotTooltip toolTip){
        this.toolTip = toolTip;
        this.currentPos = new Vector2(0,0);
        this.offset = new Vector2(20, 10);
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y){
        InventorySlot inventorySlot = (InventorySlot)event.getListenerActor();
        if( isInside ){
            currentPos.set(x, y);
            inventorySlot.localToStageCoordinates(currentPos);

            toolTip.setPosition(currentPos.x+offset.x, currentPos.y+offset.y);
        }
        return false;
    }


    @Override
    public void touchDragged (InputEvent event, float x, float y, int pointer) {
        InventorySlot inventorySlot = (InventorySlot)event.getListenerActor();
        toolTip.setVisible(inventorySlot, false);
    }

    @Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        return true;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
        InventorySlot inventorySlot = (InventorySlot)event.getListenerActor();

        isInside = true;

        currentPos.set(x, y);
        inventorySlot.localToStageCoordinates(currentPos);

        toolTip.updateDescription(inventorySlot);
        toolTip.setPosition(currentPos.x + offset.x, currentPos.y + offset.y);
        toolTip.toFront();
        toolTip.setVisible(inventorySlot, true);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
        InventorySlot inventorySlot = (InventorySlot)event.getListenerActor();
        toolTip.setVisible(inventorySlot, false);
        isInside = false;

        currentPos.set(x, y);
        inventorySlot.localToStageCoordinates(currentPos);
    }

}
