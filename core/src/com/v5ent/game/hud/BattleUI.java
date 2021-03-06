package com.v5ent.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.battle.BattleEvent;
import com.v5ent.game.battle.BattleState;
import com.v5ent.game.battle.AnimatedImage;
import com.v5ent.game.entities.Monster;
import com.v5ent.game.entities.Role;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.sfx.ShakeCamera;
import com.v5ent.game.utils.Assets;

public class BattleUI extends Window{
    private static final String TAG = BattleUI.class.getSimpleName();

    private AnimatedImage _image;
    public HUDScreen hudScreen;
    public Role player;

    private final int _enemyWidth = 96;
    private final int _enemyHeight = 96;

    private BattleState battleState = null;
    private TextButton _attackButton = null;
    private TextButton _runButton = null;
    private Label damageValLabel = null;

    private float battleTimer = 0;
    private final float _checkTimer = 1;

    private ShakeCamera battleShakeCam = null;
    private Array<ParticleEffect> _effects;

    private float _origDamageValLabelY = 0;
    private Vector2 _currentImagePosition;

    public BattleUI(HUDScreen parent,final Role player){
        super("战斗", Assets.instance.STATUSUI_SKIN);
        hudScreen = parent;
        this.player = player;
        battleTimer = 0;
        battleState = new BattleState(this);
//        battleState.addObserver(this);

        _effects = new Array<ParticleEffect>();
        _currentImagePosition = new Vector2(0,0);

        damageValLabel = new Label("0", Assets.instance.STATUSUI_SKIN);
        damageValLabel.setVisible(false);

     // + Background
        Image imgBackground = new Image(new Texture(Gdx.files.internal("battle/background.png")));
        imgBackground.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        imgBackground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug(TAG,"you are in battle!");
            }
        });
        this.addActor(imgBackground);
        
        _image = new AnimatedImage();
        _image.setTouchable(Touchable.disabled);

        Table table = new Table();
        _attackButton = new TextButton("攻击", Assets.instance.STATUSUI_SKIN, "inventory");
        _runButton = new TextButton("逃跑", Assets.instance.STATUSUI_SKIN, "inventory");
        table.add(_attackButton).pad(20, 20, 20, 20);
        table.row();
        table.add(_runButton).pad(20, 20, 20, 20);

        //layout
        this.setFillParent(true);
        this.add(damageValLabel).align(Align.left).padLeft(_enemyWidth / 2).row();
        this.add(_image).size(_enemyWidth, _enemyHeight).pad(10, 10, 10, _enemyWidth / 2);
        this.add(table);

        this.pack();

        _origDamageValLabelY = damageValLabel.getY()+_enemyHeight;
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug(TAG,"you are in battle!");
            }
        });
        _attackButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        battleState.playerAttacks(player);
                    }
                }
        );
        _runButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        battleState.playerRuns();
                    }
                }
        );
    }

    public void battleZoneTriggered(int battleZoneValue){
        battleState.setCurrentZoneLevel(battleZoneValue);
    }

    public boolean isBattleReady(){
        if( battleTimer > _checkTimer ){
            battleTimer = 0;
            return battleState.isOpponentReady();
        }else{
            return false;
        }
    }

    public BattleState getCurrentState(){
        return battleState;
    }
//    @Override
    public void updateEvent(Monster entity, BattleEvent event) {
        hudScreen.onNotify(entity,event);
        switch(event){
            case PLAYER_TURN_START:
                _runButton.setDisabled(true);
                _runButton.setTouchable(Touchable.disabled);
                _attackButton.setDisabled(true);
                _attackButton.setTouchable(Touchable.disabled);
                break;
            case OPPONENT_ADDED:
                _image.setEntity(entity);
                _image.setCurrentAnimation(Role.State.FIXED);
                _image.setSize(_enemyWidth, _enemyHeight);

                _currentImagePosition.set(_image.getX(),_image.getY());
                if( battleShakeCam == null ){
                    battleShakeCam = new ShakeCamera(_currentImagePosition.x, _currentImagePosition.y, 30.0f);
                }
                //set title todo
                this.setName("Level " + battleState.getCurrentZoneLevel() + " " + entity.getEntityId());
                break;
            case OPPONENT_HIT_DAMAGE:
//                int damage = Integer.parseInt(entity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                int damage = entity.getHitDamageTotal();
                damageValLabel.setText(String.valueOf(damage));
                damageValLabel.setY(_origDamageValLabelY);
                battleShakeCam.startShaking();
                damageValLabel.setVisible(true);
                break;
            case OPPONENT_DEFEATED:
                damageValLabel.setVisible(false);
                damageValLabel.setY(_origDamageValLabelY);
                break;
            case OPPONENT_TURN_DONE:
                 _attackButton.setDisabled(false);
                 _attackButton.setTouchable(Touchable.enabled);
                _runButton.setDisabled(false);
                _runButton.setTouchable(Touchable.enabled);
                break;
            case PLAYER_TURN_DONE:
                battleState.opponentAttacks();
                break;
            case PLAYER_USED_MAGIC:
                float x = _currentImagePosition.x + (_enemyWidth/2);
                float y = _currentImagePosition.y + (_enemyHeight/2);
//                _effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
                break;
            default:
                break;
        }
    }

    public void resetDefaults(){
        battleTimer = 0;
        battleState.resetDefaults();
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);

        //Draw the particles last
        for( int i = 0; i < _effects.size; i++){
            ParticleEffect effect = _effects.get(i);
            if( effect == null ) continue;
            effect.draw(batch);
        }
    }

    @Override
    public void act(float delta){
        battleTimer = (battleTimer + delta)%60;
        if( damageValLabel.isVisible() && damageValLabel.getY() < this.getHeight()){
            damageValLabel.setY(damageValLabel.getY()+5);
        }

        if( battleShakeCam != null && battleShakeCam.isCameraShaking() ){
            Vector2 shakeCoords = battleShakeCam.getNewShakePosition();
            _image.setPosition(shakeCoords.x, shakeCoords.y);
        }

        for( int i = 0; i < _effects.size; i++){
            ParticleEffect effect = _effects.get(i);
            if( effect == null ) continue;
            if( effect.isComplete() ){
                _effects.removeIndex(i);
                effect.dispose();
            }else{
                effect.update(delta);
            }
        }

        super.act(delta);
    }
}
