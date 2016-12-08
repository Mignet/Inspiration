package com.v5ent.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.battle.LevelTable;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.utils.Assets;

public class StatusUI extends Table {
    public static final String TAG = StatusUI.class.getName();
    private HUDScreen hudScreen;

    private Image hpBar;
    private Image mpBar;
    private Image xpBar;

    private Array<LevelTable> levelTables;
    private static final String LEVEL_TABLE_CONFIG = "data/level_tables.json";

    //Attributes
    private int levelVal = 1;
    private int goldVal = 10;
    private int hpVal = 80;
    private int mpVal = 80;
    private int xpVal = 10;

    private int xpCurrentMax = 100;
    private int hpCurrentMax = 100;
    private int mpCurrentMax = 100;

    private Label hpValLabel;
    private Label mpValLabel;
    private Label xpValLabel;
    private Label levelValLabel;
    private Label goldValLabel;

    private float _barHeight = 0;

    public StatusUI(HUDScreen parent){
        super(Assets.instance.STATUSUI_SKIN);
        hudScreen = parent;
        levelTables = LevelTable.getLevelTables(LEVEL_TABLE_CONFIG);

        //groups
        WidgetGroup group = new WidgetGroup();
        WidgetGroup group2 = new WidgetGroup();
        WidgetGroup group3 = new WidgetGroup();

        //images
        hpBar = new Image(new Texture(Gdx.files.internal("hud/hp_bar.png")));
        Image bar = new Image(new Texture(Gdx.files.internal("hud/bar.png")));
        mpBar = new Image(new Texture(Gdx.files.internal("hud/mp_bar.png")));
        Image bar2 = new Image(new Texture(Gdx.files.internal("hud/bar.png")));
        xpBar = new Image(new Texture(Gdx.files.internal("hud/xp_bar.png")));
        Image bar3 = new Image(new Texture(Gdx.files.internal("hud/bar.png")));

        Image border = new Image(new Texture(Gdx.files.internal("hud/border.png")));
        Image header = new Image(new Texture(Gdx.files.internal("hud/lante.png")));
        header.setPosition(3,3);

        _barHeight = hpBar.getHeight();
        //labels
        Label hpLabel = new Label(" 血量: ", Assets.instance.STATUSUI_SKIN);
        hpValLabel = new Label(String.valueOf(hpVal), Assets.instance.STATUSUI_SKIN);
        Label mpLabel = new Label(" 魔力: ", Assets.instance.STATUSUI_SKIN);
        mpValLabel = new Label(String.valueOf(mpVal), Assets.instance.STATUSUI_SKIN);
        Label xpLabel = new Label(" 经验: ", Assets.instance.STATUSUI_SKIN);
        xpValLabel = new Label(String.valueOf(xpVal), Assets.instance.STATUSUI_SKIN);
        Label levelLabel = new Label("等级:", Assets.instance.STATUSUI_SKIN);
        levelValLabel = new Label(String.valueOf(levelVal), Assets.instance.STATUSUI_SKIN);
        Label goldLabel = new Label(" 金币: ", Assets.instance.STATUSUI_SKIN);
        goldValLabel = new Label(String.valueOf(goldVal), Assets.instance.STATUSUI_SKIN);


        //Align images
        hpBar.setWidth(100 * (hpVal*1.0f/hpCurrentMax));
        mpBar.setWidth(100 * (mpVal*1.0f/mpCurrentMax));
        xpBar.setWidth(100 * (xpVal*1.0f/xpCurrentMax));

        //add to widget groups
        group.addActor(hpBar);
        group.addActor(bar);
        group2.addActor(mpBar);
        group2.addActor(bar2);
        group3.addActor(xpBar);
        group3.addActor(bar3);

        //Add to layout
        defaults().expand().fill();

        //top layout
        WidgetGroup headGroup = new WidgetGroup();
        headGroup.addActor(header);
        headGroup.addActor(border);
        Table leftTable = new Table();
        leftTable.add(headGroup).size(border.getWidth(), border.getHeight());
        leftTable.row();


        Table rightTable = new Table();
        rightTable.add(levelLabel).align(Align.left);
        rightTable.add(levelValLabel).align(Align.left);
//        rightTable.add(goldLabel);
//        rightTable.add(goldValLabel).align(Align.left);
        rightTable.row();

        rightTable.add(group).size(bar.getWidth(), bar.getHeight()).colspan(2);
//        rightTable.add(hpLabel);
//        rightTable.add(hpValLabel).align(Align.left);
        rightTable.row();

        rightTable.add(group2).size(bar2.getWidth(), bar2.getHeight()).colspan(2);
//        rightTable.add(mpLabel);
//        rightTable.add(mpValLabel).align(Align.left);
        rightTable.row();

        rightTable.add(group3).size(bar3.getWidth(), bar3.getHeight()).colspan(2);
//        rightTable.add(xpLabel);
//        rightTable.add(xpValLabel).align(Align.left).padRight(20);
        rightTable.row();

        this.add(leftTable);
        this.add(rightTable);

//        this.debug();
        this.pack();
        headGroup.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //nothing to do ,only catch the click
            }
        });
    }

    public int getLevelValue(){
        return levelVal;
    }
    public void setLevelValue(int levelValue){
        this.levelVal = levelValue;
        levelValLabel.setText(String.valueOf(levelVal));
        hudScreen.updateLevel(levelValue);
//        notify(levelVal, StatusObserver.StatusEvent.UPDATED_LEVEL);
    }

    public int getGoldValue(){
        return goldVal;
    }
    public void setGoldValue(int goldValue){
        this.goldVal = goldValue;
        goldValLabel.setText(String.valueOf(goldVal));
        hudScreen.updateGP(goldValue);
//        notify(goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public void addGoldValue(int goldValue){
        this.goldVal += goldValue;
        goldValLabel.setText(String.valueOf(goldVal));
        hudScreen.updateGP(goldValue);
//        notify(goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public int getXPValue(){
        return xpVal;
    }

    public void addXPValue(int xpValue){
        this.xpVal += xpValue;
        if( xpVal > xpCurrentMax ){
            updateToNewLevel();
        }
        xpValLabel.setText(String.valueOf(xpVal));
        updateBar(xpBar, xpVal, xpCurrentMax);
        hudScreen.updateXP(xpVal);
//        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValue(int xpValue){
        this.xpVal = xpValue;
        if( xpVal > xpCurrentMax ){
            updateToNewLevel();
        }
        xpValLabel.setText(String.valueOf(xpVal));
        updateBar(xpBar, xpVal, xpCurrentMax);
        hudScreen.updateXP(xpVal);
//        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValueMax(int maxXPValue){
        this.xpCurrentMax = maxXPValue;
    }

    public void setStatusForLevel(int level){
        for( LevelTable table: levelTables ){
            if( Integer.parseInt(table.getLevelID()) == level ){
                setXPValueMax(table.getXpMax());
                setXPValue(0);

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                return;
            }
        }
    }

    public void updateToNewLevel(){
        for( LevelTable table: levelTables ){
            //System.out.println("XPVAL " + xpVal + " table XPMAX " + table.getXpMax() );
            if( xpVal > table.getXpMax() ){
                continue;
            }else{
                setXPValueMax(table.getXpMax());

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                hudScreen.levelUp();
//                notify(levelVal, StatusObserver.StatusEvent.LEVELED_UP);
                return;
            }
        }
    }

    public int getXPValueMax(){
        return xpCurrentMax;
    }

    //HP
    public int getHPValue(){
        return hpVal;
    }

    public void removeHPValue(int hpValue){
        hpVal = MathUtils.clamp(hpVal - hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));
        updateBar(hpBar, hpVal, hpCurrentMax);
        hudScreen.updateHP(hpVal);
//        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void addHPValue(int hpValue){
        hpVal = MathUtils.clamp(hpVal + hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));
        updateBar(hpBar, hpVal, hpCurrentMax);
        hudScreen.updateHP(hpVal);
//        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValue(int hpValue){
        this.hpVal = hpValue;
        hpValLabel.setText(String.valueOf(hpVal));
        updateBar(hpBar, hpVal, hpCurrentMax);
        hudScreen.updateHP(hpVal);
//        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValueMax(int maxHPValue){
        this.hpCurrentMax = maxHPValue;
    }

    public int getHPValueMax(){
        return hpCurrentMax;
    }

    //MP
    public int getMPValue(){
        return mpVal;
    }

    public void removeMPValue(int mpValue){
        mpVal = MathUtils.clamp(mpVal - mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));
        updateBar(mpBar, mpVal, mpCurrentMax);
        hudScreen.updateMP(mpVal);
//        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void addMPValue(int mpValue){
        mpVal = MathUtils.clamp(mpVal + mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));
        updateBar(mpBar, mpVal, mpCurrentMax);
        hudScreen.updateMP(mpVal);
//        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValue(int mpValue){
        this.mpVal = mpValue;
        mpValLabel.setText(String.valueOf(mpVal));
        updateBar(mpBar, mpVal, mpCurrentMax);
        hudScreen.updateMP(mpVal);
//        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValueMax(int maxMPValue){
        this.mpCurrentMax = maxMPValue;
    }

    public int getMPValueMax(){
        return mpCurrentMax;
    }

    public void updateBar(Image bar, int currentVal, int maxVal){
        int val = MathUtils.clamp(currentVal, 0, maxVal);
        float tempPercent = (float) val / (float) maxVal;
        float percentage = MathUtils.clamp(tempPercent, 0, 100);
        bar.setSize(100*percentage, _barHeight);
    }

}
