package com.v5ent.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.utils.AssetsManager;

import static com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpawnShape.point;
import static sun.audio.AudioPlayer.player;

/***
 * Role contains:Character and NPC
 * It extends from Sprite,thus,owns transformation and draw
 */
public class Role extends Sprite{
	private static final String TAG = Role.class.getSimpleName();
	
//	private Vector2 velocity;
	private float speed = 4*32f;
	private String entityId;

	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation walkUpAnimation;
	private Animation walkDownAnimation;
	protected Vector2 nextPosition;
	private State currentState = State.IDLE;
	private Direction currentDir = Direction.LEFT;
	protected float frameTime = 0f;
	/**just for draw */
	protected TextureRegion currentFrame = null;
	private Array<MyNode> path = null;

	public final static int FRAME_WIDTH = 32;
	public final static int FRAME_HEIGHT = 48;
	// what role want to move
	private Vector2 targetPosition;

	public enum State {
		IDLE, WALKING
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
	}
	
	public Role(String entityId){
		init(entityId);
	}

	public void init(String entityId){
		this.entityId = entityId;
		this.nextPosition = new Vector2();
		this.targetPosition = new Vector2();
		AssetsManager.AssetRole assetRole = AssetsManager.instance.assetRoles.get(entityId);
		this.walkLeftAnimation = assetRole.walkLeftAnimation;
		this.walkRightAnimation = assetRole.walkRightAnimation;
		this.walkUpAnimation = assetRole.walkUpAnimation;
		this.walkDownAnimation = assetRole.walkDownAnimation;
		currentFrame =walkRightAnimation.getKeyFrame(0);
		// Define sprite size to be 1m x 1m in game world
		this.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
		// Set origin to sprite's center
		this.setOrigin(this.getWidth() / 2.0f, 0);
		Gdx.app.debug(TAG, "Construction :"+entityId );
	}

	public void update(float delta) {
		frameTime = (frameTime + delta) % 4; // Want to avoid overflow
		if(this.currentState==State.WALKING){
			calcNextPosition(delta);
			if(Math.abs(this.nextPosition.x-this.targetPosition.x*32f)<speed*delta && Math.abs(this.nextPosition.y-this.targetPosition.y*32f)<speed*delta){
				this.setPosInMap(targetPosition);
				if(path!=null&&path.size>0){
					MyNode p = path.pop();
					moveTo(p.getX(),p.getY());
				}else{
					this.currentState= State.IDLE;
				}

			}
		}
		updateCurrentFrame();
	}

	/**
	 * get correct frame from state and direction
	 */
	public void updateCurrentFrame() {
		// Look into the appropriate variable when changing position
		if(currentState==State.IDLE){
			switch (currentDir) {
				case UP:
					currentFrame = walkUpAnimation.getKeyFrame(1);
					break;
				case RIGHT:
					currentFrame = walkRightAnimation.getKeyFrame(1);
					break;
				case DOWN:
					currentFrame = walkDownAnimation.getKeyFrame(1);
					break;
				case LEFT:
					currentFrame = walkLeftAnimation.getKeyFrame(1);
					break;
				default:
					break;
			}
		}
		if(currentState==State.WALKING){
			switch (currentDir) {
				case UP:
					currentFrame = walkUpAnimation.getKeyFrame(frameTime);
					break;
				case RIGHT:
					currentFrame = walkRightAnimation.getKeyFrame(frameTime);
					break;
				case DOWN:
					currentFrame = walkDownAnimation.getKeyFrame(frameTime);
					break;
				case LEFT:
					currentFrame = walkLeftAnimation.getKeyFrame(frameTime);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void draw(Batch batch) {
		// Draw image
		batch.draw(currentFrame.getTexture(), getX(), getY(),getOriginX(), getOriginY(), getWidth(),getHeight(), getScaleX(), getScaleY(),
				getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}

	public void calcNextPosition(float deltaTime) {
		float testX = this.getX();
		float testY = this.getY();
		speed *= (deltaTime);
		switch (currentDir) {
			case LEFT:
				testX -= speed;
				break;
			case RIGHT:
				testX += speed;
				break;
			case UP:
				testY += speed;
				break;
			case DOWN:
				testY -= speed;
				break;
			default:
				break;
		}
		nextPosition.x = testX;
		nextPosition.y = testY;
//		Gdx.app.debug(TAG, "nextPosition:"+nextPosition);
		// velocity
		speed *=(1 / deltaTime);
		setPosition(nextPosition.x, nextPosition.y);
	}

	public void setPosInMap(Vector2 point){
		this.setPosition(point.x*32f,point.y*32f);
		targetPosition = point.cpy();
	}

	public String getEntityId() {
		return entityId;
	}

	public void moveTo(int x,int y){
		if(x< MathUtils.floor(getX()/32)){
			this.currentDir = Direction.LEFT;
		}
		if(x> MathUtils.floor(getX()/32)){
			this.currentDir = Direction.RIGHT;
		}
		if(y< MathUtils.floor(getY()/32)){
			this.currentDir = Direction.DOWN;
		}
		if(y> MathUtils.floor(getY()/32)){
			this.currentDir = Direction.UP;
		}
		this.currentState = State.WALKING;
		targetPosition = new Vector2(x,y);
	}
	public void followPath(Array<MyNode> path){
		//remove current point
		path.pop();
		this.path = path;
		MyNode point = this.path.pop();
		moveTo(point.getX(),point.getY());
	}

	public State getState() {
		return currentState;
	}

	public void setState(State currentState) {
		this.currentState = currentState;
	}

	public Direction getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(Direction currentDir) {
		this.currentDir = currentDir;
	}
}
