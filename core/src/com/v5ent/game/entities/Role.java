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
	private float speed = 0f;
	private String entityId;

	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation walkUpAnimation;
	private Animation walkDownAnimation;
	protected Vector2 nextPosition;

	protected Vector2 currentPosInMap;
	private State currentState = State.IDLE;
	private Direction currentDir = Direction.LEFT;
	protected float frameTime = 0f;
	/**for draw */
	protected TextureRegion currentFrame = null;

	public final static int FRAME_WIDTH = 32;
	public final static int FRAME_HEIGHT = 48;

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
		this.currentPosInMap = new Vector2();
		AssetsManager.AssetRole assetRole = AssetsManager.instance.assetRoles.get(entityId);
		this.walkLeftAnimation = assetRole.walkLeftAnimation;
		this.walkRightAnimation = assetRole.walkRightAnimation;
		this.walkUpAnimation = assetRole.walkUpAnimation;
		this.walkDownAnimation = assetRole.walkDownAnimation;
		currentFrame =walkRightAnimation.getKeyFrame(0);
		// Define sprite size to be 1m x 1m in game world
		this.setSize(currentFrame.getRegionWidth()/32f, currentFrame.getRegionHeight()/32);
		// Set origin to sprite's center
		this.setOrigin(this.getWidth() / 2.0f, 0);
		Gdx.app.debug(TAG, "Construction :"+entityId );
	}

	public void update(float delta) {
		frameTime = (frameTime + delta) % 4; // Want to avoid overflow
		/*if(this.currentState==State.WALKING){
			calculateNextPosition(delta);
			setNextPositionToCurrent();
			if(Math.abs(this.nextPosition.x-this.targetX)<speed*delta && Math.abs(this.nextPosition.y-this.targetY)<speed*delta){
				this.currentState= State.IDLE;
				this.setMapPosition(targetMapX,targetMapY);
			}
		}*/
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
			Gdx.app.debug(TAG, "hero's coor:"+getX()+","+getY());
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}

	public void updateCurrentFrame() {
		// Look into the appropriate variable when changing position
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
	//当没有障碍物时使用
	public void setNextPositionToCurrent() {
		setPosition(nextPosition.x, nextPosition.y);
	}

	public void calculateNextPosition(float deltaTime) {
		float testX = this.getX();
		float testY = this.getY();
		speed *=(deltaTime);
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
	}

	public void setPosInMap(Vector2 point){
		this.currentPosInMap = point.cpy();
		this.nextPosition = point.cpy().scl(MapsManager.CELL_UNIT);
	}

	public void setState(State state){
		this.currentState = state;
	}
	

	public Vector2 getCurrentPosInMap(){
		return currentPosInMap;
	}
	
	public void setDirection(Direction direction,  float deltaTime){
		this.currentDir = direction;
		
		//Look into the appropriate variable when changing position

		switch (currentDir) {
		case DOWN :
			currentFrame = walkDownAnimation.getKeyFrame(frameTime);
			break;
		case LEFT :
			currentFrame = walkLeftAnimation.getKeyFrame(frameTime);
			break;
		case UP :
			currentFrame = walkUpAnimation.getKeyFrame(frameTime);
			break;
		case RIGHT :
			currentFrame = walkRightAnimation.getKeyFrame(frameTime);
			break;
		default:
			break;
		}
	}

}
