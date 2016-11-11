package com.v5ent.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.core.AssetsManager;
import com.v5ent.game.core.MapsManager;

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
	/**for draw */
	protected TextureRegion currentFrame = null;

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
		frameTime = (frameTime + delta) % 3; // Want to avoid overflow
		if(this.currentState==State.WALKING){
			calculateNextPosition(delta);
			if(Math.abs(this.nextPosition.x-this.targetPosition.x*32f)<speed*delta && Math.abs(this.nextPosition.y-this.targetPosition.y*32f)<speed*delta){
				this.currentState= State.IDLE;
				this.setPosInMap(targetPosition);
			}
		}
	}

	@Override
	public void draw(Batch batch) {
		//		super.draw(batch);

		// Draw Particles
		//		dustParticles.draw(batch);

		// Set special color when game object has a feather power-up
		/*if (selected) {
			batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
		}*/

		// Draw image
		updateCurrentFrame();
		//		batch.draw(currentFrame.getTexture(),getX(), getY(),getWidth(),getHeight());
		batch.draw(currentFrame.getTexture(), getX(), getY(),getOriginX(), getOriginY(), getWidth(),getHeight(), getScaleX(), getScaleY(),
				getRotation(), currentFrame.getRegionX(), currentFrame.getRegionY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),false, false);
//			Gdx.app.debug(TAG, "hero's coor:"+getX()+","+getY());
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}

	public void updateCurrentFrame() {
		// Look into the appropriate variable when changing position
		if(currentState==State.IDLE){
			switch (currentDir) {
				case UP:
					currentFrame = walkUpAnimation.getKeyFrame(0);
					break;
				case RIGHT:
					currentFrame = walkRightAnimation.getKeyFrame(0);
					break;
				case DOWN:
					currentFrame = walkDownAnimation.getKeyFrame(0);
					break;
				case LEFT:
					currentFrame = walkLeftAnimation.getKeyFrame(0);
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

	public void calculateNextPosition(float deltaTime) {
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

	public Vector2 getCurrentPosInMap(){
		return new Vector2(getX(),getY());
	}
	
	public void moveOneStep(Direction direction){
		this.currentDir = direction;
		this.currentState = State.WALKING;
		//Look into the appropriate variable when changing position

		switch (currentDir) {
		case DOWN :
			targetPosition.y--;
			break;
		case LEFT :
			targetPosition.x--;
			break;
		case UP :
			targetPosition.y++;
			break;
		case RIGHT :
			targetPosition.x++;
			break;
		default:
			break;
		}
		Gdx.app.debug(TAG,"From["+getX()+","+getY()+"] to "+targetPosition.cpy().scl(32));
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
