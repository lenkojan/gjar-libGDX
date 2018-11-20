package sk.gjar.game8;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Character {

    private static final float MAX_SPEED = 0.35f;
    private static final float MAX_MOVEMENT_TIMEOUT = 0.2f;
    private static final float MAX_IDLE_TIMEOUT = 0.3f;
    private static final float VELOCITY_MARGIN = 0.05f;
    private final Body characterBody;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> deadAnimation;
    private final float characterWidth;
    private final float characterHeight;
    private CharacterState state;
    private float stateTime;
    private final TextureAtlas textureAtlas;
    private float movementStop;
    private boolean isFacingRight = true;
    private float idleTimeout;

    public Character(World world, float width, float height) {
        this.characterWidth = height / width;
        this.characterHeight = 1f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(3, 3);
        characterBody = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(characterHeight / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = characterBody.createFixture(fixtureDef);
        characterBody.setUserData(this);
        circle.dispose();


        state = CharacterState.Idle;
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
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (movementStop <= 0 && characterBody.getLinearVelocity().x != 0) {
            characterBody.setLinearVelocity(0, characterBody.getLinearVelocity().y);
        } else {
            movementStop -= deltaTime;
        }
        if (characterBody.getLinearVelocity().y > VELOCITY_MARGIN || characterBody.getLinearVelocity().y < -VELOCITY_MARGIN) {
            idleTimeout = MAX_IDLE_TIMEOUT;
            if (!state.equals(CharacterState.Jumping)) {
                state = CharacterState.Jumping;
                stateTime = 0;
            }
        } else if (characterBody.getLinearVelocity().x > VELOCITY_MARGIN || characterBody.getLinearVelocity().x < -VELOCITY_MARGIN) {
            idleTimeout = MAX_IDLE_TIMEOUT;
            if (!state.equals(CharacterState.Walking)) {
                state = CharacterState.Walking;
                stateTime = 0;
            }
        } else {
            if (idleTimeout > 0) {
                idleTimeout -= deltaTime;
            } else {
                if (state != CharacterState.Idle) {
                    state = CharacterState.Idle;
                    stateTime = 0;
                }
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
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
        float frameWidth = (float) currentFrame.getRegionWidth() / (float) currentFrame.getRegionHeight() * characterHeight;
        spriteBatch.draw(currentFrame, characterBody.getPosition().x - frameWidth / 2, characterBody.getPosition().y - characterHeight / 2, frameWidth, characterHeight);
    }

    public void jump() {
        if (state != CharacterState.Jumping) {
            characterBody.applyForceToCenter(0, 100f, true);
        }
    }

    public void moveLeft() {
        movementStop = MAX_MOVEMENT_TIMEOUT;
        if (characterBody.getLinearVelocity().x > -MAX_SPEED) {
            characterBody.applyForceToCenter(-40f, 0f, true);

            if (isFacingRight) {
                flipAnimations();
                isFacingRight = false;
            }
        }
    }

    public void moveRight() {
        movementStop = MAX_MOVEMENT_TIMEOUT;
        if (characterBody.getLinearVelocity().x < MAX_SPEED) {
            characterBody.applyForceToCenter(40f, 0f, true);

            if (!isFacingRight) {
                flipAnimations();
                isFacingRight = true;
            }
        }
    }

    private enum CharacterState {
        Idle, Walking, Jumping, Dead
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
}
