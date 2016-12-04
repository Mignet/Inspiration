package com.v5ent.game.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class InventorySlotTooltip extends Window {

    private Label desc;

    public InventorySlotTooltip(final Skin skin){
        super("", skin);
        desc = new Label("", skin);

        this.add(desc);
        this.padLeft(25).padRight(5);
        this.pack();
        this.setVisible(false);
    }

    public void setVisible(InventorySlot inventorySlot, boolean visible) {
        super.setVisible(visible);

        if( inventorySlot == null ){
            return;
        }

        if (!inventorySlot.hasItem()) {
            super.setVisible(false);
        }
    }

    public void updateDescription(InventorySlot inventorySlot){
        if( inventorySlot.hasItem() ){
            StringBuilder string = new StringBuilder();
            InventoryItem item = inventorySlot.getTopInventoryItem();
            string.append(item.getItemShortDescription());
            if( item.isInventoryItemOffensive() ){
                string.append(System.getProperty("line.separator"));
                string.append(String.format("Attack Points: %s", item.getItemUseTypeValue()));
            }else if( item.isInventoryItemDefensive() ){
                string.append(System.getProperty("line.separator"));
                string.append(String.format("Defense Points: %s", item.getItemUseTypeValue()));
            }
            string.append(System.getProperty("line.separator"));
            string.append(String.format("Original Value: %s GP", item.getItemValue()));
            string.append(System.getProperty("line.separator"));
            string.append(String.format("Trade Value: %s GP", item.getTradeValue()));

            desc.setText(string);
            this.pack();
        }else{
            desc.setText("");
            this.pack();
        }

    }
}
