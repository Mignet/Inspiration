package com.v5ent.game.entities;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.core.MapsManager;

/***
 * Player contains:Character and NPC
 * It extends from Sprite,thus,owns transformation and draw
 */
public class Player extends Sprite{
	private static final String TAG = Player.class.getSimpleName();
	
	private Vector2 velocity;
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

	public final int FRAME_WIDTH = 32;
	public final int FRAME_HEIGHT = 48;

	public enum State {
		IDLE, WALKING
	}
	
	public enum Direction {
		UP,RIGHT,DOWN,LEFT;
	}
	
	public Player(){
		init();
	}

	@Override
	public void draw(Batch batch) {
		super.draw(batch);
	}

	public void init(){
		this.entityId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		this.nextPosition = new Vector2();
		this.currentPosInMap = new Vector2();
		this.velocity = new Vector2(4* MapsManager.CELL_UNIT,4* MapsManager.CELL_UNIT);

		Gdx.app.debug(TAG, "Construction :"+entityId );
	}

	public void update(float delta){
		frameTime = (frameTime + delta)%5; //Want to avoid overflow

		//Gdx.app.debug(TAG, "frametime: " + frameTime );

		//We want the hitbox to be at the feet for a better feel
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
	

	public void calculateNextPosition(Direction currentDirection, float deltaTime){
		float testX = getX();
		float testY = getY();

		//Gdx.app.debug(TAG, "calculateNextPosition:: Current Position: (" + currentPos.x + "," + currentPos.y + ")"  );
		//Gdx.app.debug(TAG, "calculateNextPosition:: Current Direction: " + currentDir  );
		
		velocity.scl(deltaTime);
		
		switch (currentDirection) {
		case LEFT : 
		testX -=  velocity.x;
		break;
		case RIGHT :
		testX += velocity.x;
		break;
		case UP : 
		testY += velocity.y;
		break;
		case DOWN : 
		testY -= velocity.y;
		break;
		default:
			break;
		}
		
		nextPosition.x = testX;
		nextPosition.y = testY;
		
		//velocity
		velocity.scl(1 / deltaTime);
	}

	public Vector2 getNextPosition() {
		return nextPosition;
	}

	public void setNextPosition(Vector2 nextPosition) {
		this.nextPosition = nextPosition;
	}


}
