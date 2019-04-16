package sk.gjar.game11;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Character {

    private static final float MAX_SPEED = 0.35f;
    private static final float MAX_MOVEMENT_TIMEOUT = 0.2f;
    private static final float VELOCITY_MARGIN = 0.05f;
    private static final float OFFSET_X = 0.25f;
    private static final float OFFSET_Y = -0.13f;
    private static final float MAX_JUMP_SPEED = 1.7f;
    private final Body characterBody;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> deadAnimation;
    private final float characterHeight;
    private final Fixture fixture;
    private final TextureAtlas textureAtlas;
    private CharacterState state;
    private float stateTime;
    private float movementStop;
    private boolean isFacingRight = true;
    private boolean grounded;

    public Character(World world) {
        this.characterHeight = 1f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(1, 3);
        characterBody = world.createBody(bodyDef);
        characterBody.setBullet(true);

        PolygonShape characterBox = new PolygonShape();
        characterBox.setAsBox(0.4f, 0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = characterBox;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        fixture = characterBody.createFixture(fixtureDef);
        characterBody.setUserData(this);
        characterBox.dispose();


        state = CharacterState.Idle;
        stateTime = 0;
        textureAtlas = new TextureAtlas("dino.atlas");
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
        if ((characterBody.getLinearVelocity().x > VELOCITY_MARGIN || characterBody.getLinearVelocity().x < -VELOCITY_MARGIN) && (state != CharacterState.Jumping)) {
            if (!state.equals(CharacterState.Walking)) {
                state = CharacterState.Walking;
                stateTime = 0;
            }
        } else if (state != CharacterState.Jumping) {
            if (state != CharacterState.Idle) {
                state = CharacterState.Idle;
                stateTime = 0;
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
        spriteBatch.draw(currentFrame, characterBody.getPosition().x - frameWidth / 2 + (OFFSET_X * (isFacingRight ? 1 : -1)), characterBody.getPosition().y - characterHeight / 2 + OFFSET_Y, frameWidth, characterHeight);
    }

    public void jump() {
        if (state != CharacterState.Jumping && grounded) {
            characterBody.applyForceToCenter(0, 80f, true);
            if (characterBody.getLinearVelocity().y > MAX_JUMP_SPEED) {
                characterBody.setLinearVelocity(characterBody.getLinearVelocity().x,MAX_JUMP_SPEED);
            }
            state = CharacterState.Jumping;
            stateTime = 0;
        }
    }

    public void moveLeft() {
        movementStop = MAX_MOVEMENT_TIMEOUT;
        characterBody.applyForceToCenter(-40f, 0f, true);
        if (characterBody.getLinearVelocity().x < -MAX_SPEED) {
            characterBody.setLinearVelocity(-MAX_SPEED, characterBody.getLinearVelocity().y);
        }
        if (isFacingRight) {
            flipAnimations();
            isFacingRight = false;
        }
    }

    public void moveRight() {
        movementStop = MAX_MOVEMENT_TIMEOUT;
        characterBody.applyForceToCenter(40f, 0f, true);
        if (characterBody.getLinearVelocity().x > MAX_SPEED) {
            characterBody.setLinearVelocity(MAX_SPEED, characterBody.getLinearVelocity().y);
        }
        if (!isFacingRight) {
            flipAnimations();
            isFacingRight = true;
        }
    }

    public Fixture getSensorFixture() {
        return fixture;
    }

    public Vector2 getPosition() {
        return characterBody.getPosition();
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
        if (grounded && state == CharacterState.Jumping) {
            state = CharacterState.Idle;
            stateTime = 0;
        }
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

    private enum CharacterState {
        Idle, Walking, Jumping, Dead
    }
}
