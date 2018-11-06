package sk.gjar.game6;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Character {

    private static final float MAX_STOP_TIMEOUT = 0.2f;
    private static final float MAX_JUMP_TIME = 1.2f;

    static final int JUMP_SPEED = 500;
    static final int SPEED = 220;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> deadAnimation;
    private final TextureAtlas textureAtlas;
    private final Rectangle characterBounds;
    private final Rectangle activityBounds;
    private float stateTime;
    private CharacterState state = CharacterState.Idle;
    private float stopTimeout = 0;
    private boolean isFacingRight = true;
    private float jumpTime = 0;

    public Character() {
        stateTime = 0;
        textureAtlas = new TextureAtlas("santa.atlas");
        idleAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        walkAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Walk"), Animation.PlayMode.LOOP);
        jumpAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Jump"), Animation.PlayMode.NORMAL);
        deadAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Dead"), Animation.PlayMode.NORMAL);
        characterBounds = new Rectangle(200, Level.TILE_HEIGHT - 20, 150, 190);
        activityBounds = new Rectangle(200, Level.TILE_HEIGHT - 20, 880, 600);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (stopTimeout > 0)
            stopTimeout -= deltaTime;
        if (state.equals(CharacterState.Jumping)) {

            jumpTime -= deltaTime;
            if (jumpTime > MAX_JUMP_TIME / 2f) {
                //Up
                moveUp(deltaTime);
            } else {
                //Down
                if (!moveDown(deltaTime)) {
                    state = CharacterState.Idle;
                }
            }

        }
        if (stopTimeout > 0) {
            if (isFacingRight) {
                moveRight(deltaTime);
            } else {
                moveLeft(deltaTime);
            }
        } else if (stopTimeout <= 0 && !state.equals(CharacterState.Jumping)) {
            state = CharacterState.Idle;
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        switch (state) {
            case Dead:
                currentFrame = deadAnimation.getKeyFrame(stateTime, false);
                break;
            case Jumping:
                currentFrame = jumpAnimation.getKeyFrame(stateTime, false);
                break;
            case Walking:
                currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                break;
            default:
                currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }
        batch.draw(currentFrame, characterBounds.x, characterBounds.y);
    }

    public void handleAction(CharacterAction action) {
        switch (action) {
            case Jump:
                handleJump();
                break;
            case MoveLeft:
                handleMoveLeft();
                break;
            case MoveRight:
                handleMoveRight();
                break;
        }
    }

    private void handleJump() {
        if (!state.equals(CharacterState.Jumping)) {
            state = CharacterState.Jumping;
            stateTime = 0;
            jumpTime = MAX_JUMP_TIME;
        }
    }

    private void handleMoveRight() {
        if (!isFacingRight) {
            flipAnimations();
        }
        isFacingRight = true;
        if (state != CharacterState.Jumping && state != CharacterState.Walking) {
            state = CharacterState.Walking;
            stateTime = 0;
        }
        stopTimeout = MAX_STOP_TIMEOUT;
    }

    private void handleMoveLeft() {
        if (isFacingRight) {
            flipAnimations();
        }
        isFacingRight = false;
        if (state != CharacterState.Jumping && state != CharacterState.Walking) {
            state = CharacterState.Walking;
            stateTime = 0;
        }
        stopTimeout = MAX_STOP_TIMEOUT;
    }

    private void flipAnimations() {
        for (int i = 0; i < idleAnimation.getKeyFrames().length; i++) {
            idleAnimation.getKeyFrames()[i].flip(true, false);
        }
        for (int i = 0; i < walkAnimation.getKeyFrames().length; i++) {
            walkAnimation.getKeyFrames()[i].flip(true, false);
        }
        for (int i = 0; i < jumpAnimation.getKeyFrames().length; i++) {
            jumpAnimation.getKeyFrames()[i].flip(true, false);
        }
        for (int i = 0; i < deadAnimation.getKeyFrames().length; i++) {
            deadAnimation.getKeyFrames()[i].flip(true, false);
        }
    }

    private void moveRight(float deltaTime) {
        float newZombiePosition = characterBounds.x + SPEED * deltaTime;
        if (newZombiePosition + 130 < activityBounds.x + activityBounds.width) {
            characterBounds.setX(newZombiePosition);
        }
    }

    private void moveLeft(float deltaTime) {
        float newZombiePosition = characterBounds.x - SPEED * deltaTime;
        if (newZombiePosition > activityBounds.x) {
            characterBounds.setX(newZombiePosition);
        }
    }

    private void moveUp(float deltaTime) {
        float newZombiePosition = characterBounds.y + JUMP_SPEED * deltaTime;
        if (newZombiePosition < activityBounds.y + activityBounds.height) {
            characterBounds.setY(newZombiePosition);
        }
    }

    private boolean moveDown(float deltaTime) {
        float newZombiePosition = characterBounds.y - JUMP_SPEED * deltaTime;
        if (newZombiePosition > activityBounds.y) {
            characterBounds.setY(newZombiePosition);
            return true;
        } else {
            characterBounds.setY(activityBounds.y);
            return false;
        }
    }

    public void drawBounds(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(activityBounds.x, activityBounds.y, activityBounds.width, activityBounds.height);
        shapeRenderer.rect(characterBounds.x, characterBounds.y, characterBounds.width, characterBounds.height);
    }

    public void dispose() {
        textureAtlas.dispose();
    }

    private enum CharacterState {
        Idle, Walking, Jumping, Dead
    }

    public enum CharacterAction {
        MoveLeft, MoveRight, Jump, Eat
    }
}
