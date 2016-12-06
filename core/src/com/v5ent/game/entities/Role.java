package com.v5ent.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.v5ent.game.inventory.InventoryItem;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.utils.AssetsManager;

import java.util.ArrayList;

/***
 * Role contains:Character and NPC
 * It extends from Sprite,thus,owns transformation and draw
 */
public class Role extends Sprite{
	private static final String TAG = Role.class.getSimpleName();
	private static Json json = new Json();
//	private Vector2 velocity;
	private float speed = 4*32f;
	private String entityId;
	/** path is arrived **/
	private boolean isArrived = false;
	private boolean isSelected = false;

	private Animation idleLeftAnimation;
	private Animation idleRightAnimation;
	private Animation idleUpAnimation;
	private Animation idleDownAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation walkUpAnimation;
	private Animation walkDownAnimation;
	/**temp position**/
	protected Vector2 nextPosition;
	private State currentState = State.IDLE;
	private Direction currentDir = Direction.LEFT;
	protected float frameTime = 0f;
	/**just for draw */
	protected TextureRegion currentFrame = null;
	public Array<MyNode> path = new Array<MyNode>(true,10);
	// move to target
	private Vector2 movingTarget;
	//InventoryItem
	private String questConfigPath;
	private String currentQuestID;
	private String itemTypeID = "NONE";
	// role inventory
	private Array<InventoryItem.ItemTypeID> inventory = new Array<InventoryItem.ItemTypeID>();
	

	public Animation getAnimation(State animationType) {
		if(currentState==State.IDLE || currentState==State.FIXED){
			switch (currentDir) {
				case UP:
					return walkUpAnimation;
				case RIGHT:
					return walkRightAnimation;
				case DOWN:
					return walkDownAnimation;
				case LEFT:
					return walkLeftAnimation;
				default:
					break;
			}
		}
		if(currentState==State.WALKING){
			switch (currentDir) {
				case UP:
					return walkUpAnimation;
				case RIGHT:
					return walkRightAnimation;
				case DOWN:
					return walkDownAnimation;
				case LEFT:
					return walkLeftAnimation;
				default:
					break;
			}
		}
		return null;
	}

	public enum State {
		FIXED,IDLE, WALKING
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
		this.movingTarget = new Vector2();
		AssetsManager.AssetRole assetRole = AssetsManager.instance.assetRoles.get(entityId);
		this.idleLeftAnimation = assetRole.idleLeftAnimation;
		this.idleRightAnimation = assetRole.idleRightAnimation;
		this.idleUpAnimation = assetRole.idleUpAnimation;
		this.idleDownAnimation = assetRole.idleDownAnimation;
		this.walkLeftAnimation = assetRole.walkLeftAnimation;
		this.walkRightAnimation = assetRole.walkRightAnimation;
		this.walkUpAnimation = assetRole.walkUpAnimation;
		this.walkDownAnimation = assetRole.walkDownAnimation;
		currentFrame =walkRightAnimation.getKeyFrame(0);
		// Define sprite size to be 1m x 1m in game world
		this.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
		// Set origin to sprite's center
		this.setOrigin(this.getWidth() / 2.0f, 0);
		//inventory
		initInventory("items/"+entityId+".json");
		Gdx.app.debug(TAG, "Construction :"+entityId );
	}

	/**
	 * load from json config
	 */
	public void initInventory(String itemsPath){
		if(Gdx.files.internal(itemsPath).exists()) {
			Array<InventoryItem.ItemTypeID> list = json.fromJson(Array.class, Gdx.files.internal(itemsPath));
			//inventory
			for (int i = 0; i < list.size; i++) {
				inventory.add(list.get(i));
			}
		}
	}
	/**
	 * if walking,just move to center
	 * @param delta
     */
	public void update(float delta) {
		frameTime = (frameTime + delta) % 4; // Want to avoid overflow
		if(this.currentState==State.WALKING){
			calcNextPosition(delta);
			if(Math.abs(this.nextPosition.x-this.movingTarget.x*32f)<speed*delta && Math.abs(this.nextPosition.y-this.movingTarget.y*32f)<speed*delta){
				this.setPosInMap(movingTarget);
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
		if(currentState==State.IDLE || currentState==State.FIXED){
			switch (currentDir) {
				case UP:
					currentFrame = idleUpAnimation.getKeyFrame(0);
					break;
				case RIGHT:
					currentFrame = idleRightAnimation.getKeyFrame(0);
					break;
				case DOWN:
					currentFrame = idleDownAnimation.getKeyFrame(0);
					break;
				case LEFT:
					currentFrame = idleLeftAnimation.getKeyFrame(0);
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
		if(isSelected){
			//Draw selected
			batch.draw(AssetsManager.instance.selected,getX(),getY()-2);
		}else{
			batch.draw(AssetsManager.instance.shadow,getX(),getY()-2);
		}
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
		Vector2 v = new Vector2(movingTarget.x*32f-testX,movingTarget.y*32f-testY).nor();
		nextPosition.x = testX + v.x*speed;
		nextPosition.y = testY + v.y*speed;
//		Gdx.app.debug(TAG, "nextPosition:"+nextPosition);
		// velocity
		speed *=(1 / deltaTime);
		setPosition(nextPosition.x, nextPosition.y);
	}

	public void setPosInMap(Vector2 point){
		this.setPosition(point.x*32f,point.y*32f);
		movingTarget = point.cpy();
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
	public String getItemTypeID() {
		return itemTypeID;
	}

	public void setItemTypeID(String itemTypeID) {
		this.itemTypeID = itemTypeID;
	}

	public Array<InventoryItem.ItemTypeID> getInventory() {
		return inventory;
	}

	public void setInventory(Array<InventoryItem.ItemTypeID> inventory) {
		this.inventory = inventory;
	}
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
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
		movingTarget = new Vector2(x,y);
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
