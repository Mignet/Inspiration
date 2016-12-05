package com.v5ent.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.battle.LevelTable;
import com.v5ent.game.utils.AssetsManager;

public class ButtonUI extends Table {

    private ImageButton inventoryButton;
    private ImageButton questButton;

    public ButtonUI(){
        super(AssetsManager.instance.STATUSUI_SKIN);
        //buttons
        inventoryButton= new ImageButton(AssetsManager.instance.STATUSUI_SKIN, "inventory-button");
        inventoryButton.getImageCell().size(32, 32);

        questButton = new ImageButton(AssetsManager.instance.STATUSUI_SKIN, "quest-button");
        questButton.getImageCell().size(32,32);

        //Add to layout
        defaults().expand().fill();

        //UI
        Table topTable = new Table();
        topTable.add(questButton);
        topTable.add(inventoryButton);
        topTable.row();

        this.add(topTable);

        this.debug();
        this.pack();
    }

    public ImageButton getInventoryButton() {
        return inventoryButton;
    }

    public ImageButton getQuestButton() {
        return questButton;
    }

}
