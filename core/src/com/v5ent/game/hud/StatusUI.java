package com.v5ent.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.battle.LevelTable;
import com.v5ent.game.utils.AssetsManager;

public class StatusUI extends Window {
    private Image _hpBar;
    private Image _mpBar;
    private Image _xpBar;

    private ImageButton _inventoryButton;
    private ImageButton _questButton;

    private Array<LevelTable> _levelTables;
    private static final String LEVEL_TABLE_CONFIG = "data/level_tables.json";

    //Attributes
    private int _levelVal = 1;
    private int _goldVal = 10;
    private int _hpVal = 100;
    private int _mpVal = 80;
    private int _xpVal = 0;

    private int _xpCurrentMax = 100;
    private int _hpCurrentMax = 100;
    private int _mpCurrentMax = 100;

    private Label _hpValLabel;
    private Label _mpValLabel;
    private Label _xpValLabel;
    private Label _levelValLabel;
    private Label _goldValLabel;

    private float _barWidth = 0;
    private float _barHeight = 0;

    public StatusUI(){
        super("状态", AssetsManager.instance.STATUSUI_SKIN);

        _levelTables = LevelTable.getLevelTables(LEVEL_TABLE_CONFIG);

//        _observers = new Array<StatusObserver>();

        //groups
        WidgetGroup group = new WidgetGroup();
        WidgetGroup group2 = new WidgetGroup();
        WidgetGroup group3 = new WidgetGroup();

        //images
        _hpBar = new Image(new Texture(Gdx.files.internal("hud/hp_bar.png")));
        Image bar = new Image(new Texture(Gdx.files.internal("hud/bar.png")));
        _mpBar = new Image(new Texture(Gdx.files.internal("hud/mp_bar.png")));
        Image bar2 = new Image(new Texture(Gdx.files.internal("hud/bar.png")));
        _xpBar = new Image(new Texture(Gdx.files.internal("hud/mp_bar.png")));
        Image bar3 = new Image(new Texture(Gdx.files.internal("hud/bar.png")));

        Image header = new Image(new Texture(Gdx.files.internal("heros/header1.png")));

        _barWidth = _hpBar.getWidth();
        _barHeight = _hpBar.getHeight();


        //labels
        Label hpLabel = new Label(" 血量: ", AssetsManager.instance.STATUSUI_SKIN);
        _hpValLabel = new Label(String.valueOf(_hpVal), AssetsManager.instance.STATUSUI_SKIN);
        Label mpLabel = new Label(" 魔力: ", AssetsManager.instance.STATUSUI_SKIN);
        _mpValLabel = new Label(String.valueOf(_mpVal), AssetsManager.instance.STATUSUI_SKIN);
        Label xpLabel = new Label(" 经验: ", AssetsManager.instance.STATUSUI_SKIN);
        _xpValLabel = new Label(String.valueOf(_xpVal), AssetsManager.instance.STATUSUI_SKIN);
        Label levelLabel = new Label(" 等级: ", AssetsManager.instance.STATUSUI_SKIN);
        _levelValLabel = new Label(String.valueOf(_levelVal), AssetsManager.instance.STATUSUI_SKIN);
        Label goldLabel = new Label(" 金币: ", AssetsManager.instance.STATUSUI_SKIN);
        _goldValLabel = new Label(String.valueOf(_goldVal), AssetsManager.instance.STATUSUI_SKIN);

        //buttons
        _inventoryButton= new ImageButton(AssetsManager.instance.STATUSUI_SKIN, "inventory-button");
        _inventoryButton.getImageCell().size(32, 32);

        _questButton = new ImageButton(AssetsManager.instance.STATUSUI_SKIN, "quest-button");
        _questButton.getImageCell().size(32,32);

        //Align images
        _hpBar.setWidth(_barWidth * _hpVal/_hpCurrentMax);
        _mpBar.setWidth(_barWidth * _mpVal/_mpCurrentMax);
        _xpBar.setWidth(_barWidth * _xpVal/_xpCurrentMax);
//        _hpBar.setPosition(3, 6);
//        _mpBar.setPosition(3, 6);
//        _xpBar.setPosition(3, 6);
//        bar.setPosition(3, 6);
//        bar2.setPosition(3, 6);
//        bar3.setPosition(3, 6);

        //add to widget groups
        group.addActor(_hpBar);
        group.addActor(bar);
        group2.addActor(_mpBar);
        group2.addActor(bar2);
        group3.addActor(_xpBar);
        group3.addActor(bar3);

        //Add to layout
        defaults().expand().fill();

        //account for the title padding
        this.pad(this.getPadTop() + 10, 10, 10, 10);

        this.add(header).size(header.getWidth(), header.getHeight());
//        this.add(header).align(Align.left);
        this.add(_questButton).align(Align.center);
        this.add(_inventoryButton).align(Align.right).padRight(10);
        this.row();

        this.add(group).size(bar.getWidth(), bar.getHeight()).padRight(10);
        this.add(hpLabel);
        this.add(_hpValLabel).align(Align.left);
        this.row();

        this.add(group2).size(bar2.getWidth(), bar2.getHeight()).padRight(10);
        this.add(mpLabel);
        this.add(_mpValLabel).align(Align.left);
        this.row();

        this.add(group3).size(bar3.getWidth(), bar3.getHeight()).padRight(10);
        this.add(xpLabel);
        this.add(_xpValLabel).align(Align.left).padRight(20);
        this.row();

        this.add(levelLabel).align(Align.left);
        this.add(_levelValLabel).align(Align.left);
        this.row();
        this.add(goldLabel);
        this.add(_goldValLabel).align(Align.left);

//        this.debug();
        this.pack();
    }

    public ImageButton getInventoryButton() {
        return _inventoryButton;
    }

    public ImageButton getQuestButton() {
        return _questButton;
    }

    public int getLevelValue(){
        return _levelVal;
    }
    public void setLevelValue(int levelValue){
        this._levelVal = levelValue;
        _levelValLabel.setText(String.valueOf(_levelVal));
//        notify(_levelVal, StatusObserver.StatusEvent.UPDATED_LEVEL);
    }

    public int getGoldValue(){
        return _goldVal;
    }
    public void setGoldValue(int goldValue){
        this._goldVal = goldValue;
        _goldValLabel.setText(String.valueOf(_goldVal));
//        notify(_goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public void addGoldValue(int goldValue){
        this._goldVal += goldValue;
        _goldValLabel.setText(String.valueOf(_goldVal));
//        notify(_goldVal, StatusObserver.StatusEvent.UPDATED_GP);
    }

    public int getXPValue(){
        return _xpVal;
    }

    public void addXPValue(int xpValue){
        this._xpVal += xpValue;

        if( _xpVal > _xpCurrentMax ){
            updateToNewLevel();
        }

        _xpValLabel.setText(String.valueOf(_xpVal));

        updateBar(_xpBar, _xpVal, _xpCurrentMax);

//        notify(_xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValue(int xpValue){
        this._xpVal = xpValue;

        if( _xpVal > _xpCurrentMax ){
            updateToNewLevel();
        }

        _xpValLabel.setText(String.valueOf(_xpVal));

        updateBar(_xpBar, _xpVal, _xpCurrentMax);

//        notify(_xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValueMax(int maxXPValue){
        this._xpCurrentMax = maxXPValue;
    }

    public void setStatusForLevel(int level){
        for( LevelTable table: _levelTables ){
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
        for( LevelTable table: _levelTables ){
            //System.out.println("XPVAL " + _xpVal + " table XPMAX " + table.getXpMax() );
            if( _xpVal > table.getXpMax() ){
                continue;
            }else{
                setXPValueMax(table.getXpMax());

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
//                notify(_levelVal, StatusObserver.StatusEvent.LEVELED_UP);
                return;
            }
        }
    }

    public int getXPValueMax(){
        return _xpCurrentMax;
    }

    //HP
    public int getHPValue(){
        return _hpVal;
    }

    public void removeHPValue(int hpValue){
        _hpVal = MathUtils.clamp(_hpVal - hpValue, 0, _hpCurrentMax);
        _hpValLabel.setText(String.valueOf(_hpVal));

        updateBar(_hpBar, _hpVal, _hpCurrentMax);

//        notify(_hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void addHPValue(int hpValue){
        _hpVal = MathUtils.clamp(_hpVal + hpValue, 0, _hpCurrentMax);
        _hpValLabel.setText(String.valueOf(_hpVal));

        updateBar(_hpBar, _hpVal, _hpCurrentMax);

//        notify(_hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValue(int hpValue){
        this._hpVal = hpValue;
        _hpValLabel.setText(String.valueOf(_hpVal));

        updateBar(_hpBar, _hpVal, _hpCurrentMax);

//        notify(_hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValueMax(int maxHPValue){
        this._hpCurrentMax = maxHPValue;
    }

    public int getHPValueMax(){
        return _hpCurrentMax;
    }

    //MP
    public int getMPValue(){
        return _mpVal;
    }

    public void removeMPValue(int mpValue){
        _mpVal = MathUtils.clamp(_mpVal - mpValue, 0, _mpCurrentMax);
        _mpValLabel.setText(String.valueOf(_mpVal));

        updateBar(_mpBar, _mpVal, _mpCurrentMax);

//        notify(_mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void addMPValue(int mpValue){
        _mpVal = MathUtils.clamp(_mpVal + mpValue, 0, _mpCurrentMax);
        _mpValLabel.setText(String.valueOf(_mpVal));

        updateBar(_mpBar, _mpVal, _mpCurrentMax);

//        notify(_mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValue(int mpValue){
        this._mpVal = mpValue;
        _mpValLabel.setText(String.valueOf(_mpVal));

        updateBar(_mpBar, _mpVal, _mpCurrentMax);

//        notify(_mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValueMax(int maxMPValue){
        this._mpCurrentMax = maxMPValue;
    }

    public int getMPValueMax(){
        return _mpCurrentMax;
    }

    public void updateBar(Image bar, int currentVal, int maxVal){
        int val = MathUtils.clamp(currentVal, 0, maxVal);
        float tempPercent = (float) val / (float) maxVal;
        float percentage = MathUtils.clamp(tempPercent, 0, 100);
        bar.setSize(_barWidth*percentage, _barHeight);
    }

}
