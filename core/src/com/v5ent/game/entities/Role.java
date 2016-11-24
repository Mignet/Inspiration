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

/***
 * Role contains:Character and NPC
 * It extends from Sprite,thus,owns transformation and draw
 */
public class Role extends Sprite{
	private static final String TAG = Role.class.getSimpleName();
	
//	private Vector2 velocity;
	private float speed = 4*32f;
	private String entityId;
	/** path is arrived **/
	private boolean isArrived = false;

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
	public Array<MyNode> path = new Array<MyNode>(true,10);

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

	/**
	 * if walking,just move to center
	 * @param delta
     */
	public void update(float delta) {
		frameTime = (frameTime + delta) % 4; // Want to avoid overflow
		if(this.currentState==State.WALKING){
			calcNextPosition(delta);
			if(Math.abs(this.nextPosition.x-this.targetPosition.x*32f)<speed*delta && Math.abs(this.nextPosition.y-this.targetPosition.y*32f)<speed*delta){
				this.setPosInMap(targetPosition);
				if(path!=null&&path.size>0){
					MyNode nextPoint = path.pop();
					moveTo(nextPoint.getX(),nextPoint.getY());
				}else{
					isArrived = true;
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

	/**
	 * move every delta time on speed
	 * @param deltaTime
     */
	public void calcNextPosition(float deltaTime) {
		float testX = this.getX();
		float testY = this.getY();
		speed *= (deltaTime);
		Vector2 v = new Vector2(targetPosition.x*32f-testX,targetPosition.y*32f-testY).nor();
		nextPosition.x = testX + v.x*speed;
		nextPosition.y = testY + v.y*speed;
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
	public boolean isArrived() {
		return isArrived;
	}

	public void setArrived(boolean arrived) {
		isArrived = arrived;
	}

	/**
	* move Role to x,y
	* @param x
	* @param y
	*/
	public void moveTo(int x,int y){
		int gapX = x - MathUtils.floor(getX()/32);
		int gapY = y - MathUtils.floor(getY()/32);
		if(Math.abs(gapX)<Math.abs(gapY)){
			if(gapY<0){
				this.currentDir = Direction.DOWN;
			}else{
				this.currentDir = Direction.UP;
			}
		}else{
			if(gapX<0){
				this.currentDir = Direction.LEFT;
			}else{
				this.currentDir = Direction.RIGHT;
			}
		}
		this.currentState = State.WALKING;
		targetPosition = new Vector2(x,y);
		isArrived = false;
	}
	public void followPath(Array<MyNode> newPath){
		if(this.path.size<=0){
			//remove current point
			newPath.pop();
		}
		this.path.clear();
		for(MyNode node:newPath){
			this.path.add(node);
		}
		if(this.path.size>0) {
			MyNode nextPoint = this.path.pop();
			moveTo(nextPoint.getX(), nextPoint.getY());
		}
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
