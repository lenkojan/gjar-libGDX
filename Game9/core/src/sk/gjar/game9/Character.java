package sk.gjar.game9;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.physics.box2d.World;

public class Character {

    private static final float MAX_SPEED = 1f;
    private final Body characterBody;
    private final Fixture characterPhysicsFixture;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> deadAnimation;
    private final float characterHeight;
    private CharacterState state;
    private float stateTime;
    private final TextureAtlas textureAtlas;

    private boolean isFacingRight = true;
    private boolean grounded;
    private long lastGroundTime;
    private float stillTime;
    private boolean jump;


    public Character(World world) {
        this.characterHeight = 1f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1, 3);
        characterBody = world.createBody(bodyDef);
        characterBody.setBullet(true);
        characterBody.setFixedRotation(true);
        characterBody.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(characterHeight / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        characterPhysicsFixture = characterBody.createFixture(fixtureDef);
        circle.dispose();
/*
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(0.25f, 0.48f);
        characterPhysicsFixture = characterBody.createFixture(poly, 1);
        poly.dispose();
        */
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
        characterBody.setAwake(true);
        Vector2 vel = characterBody.getLinearVelocity();
        Vector2 pos = characterBody.getPosition();

        if (grounded) {
            lastGroundTime = System.nanoTime();
        } else {
            if (System.nanoTime() - lastGroundTime < 100000000) {
                grounded = true;
            }
        }

        // cap max velocity on x
        if (Math.abs(vel.x) > MAX_SPEED) {
            vel.x = Math.signum(vel.x) * MAX_SPEED;
            characterBody.setLinearVelocity(vel.x, vel.y);
        }

        // calculate stilltime & damp
        if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            stillTime += Gdx.graphics.getDeltaTime();
            characterBody.setLinearVelocity(vel.x * 0.9f, vel.y);
        } else {
            stillTime = 0;
        }

        // disable friction while jumping
        if (!grounded) {
            characterPhysicsFixture.setFriction(0f);
            //characterSensorFixture.setFriction(0f);
        } else {
            if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && stillTime > 0.2) {
                characterPhysicsFixture.setFriction(100f);
                //characterSensorFixture.setFriction(100f);
            } else {
                characterPhysicsFixture.setFriction(0.2f);
                //characterSensorFixture.setFriction(0.2f);
            }
        }

        // apply left impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -MAX_SPEED) {
            characterBody.applyLinearImpulse(-2f, 0, pos.x, pos.y, true);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < MAX_SPEED) {
            characterBody.applyLinearImpulse(2f, 0, pos.x, pos.y, true);
        }

        // jump, but only when grounded
        if (jump) {
            jump = false;
            if (grounded) {
                characterBody.setLinearVelocity(vel.x, 0);
                System.out.println("jump before: " + characterBody.getLinearVelocity());
                characterBody.setTransform(pos.x, pos.y + 0.01f, 0);
                characterBody.applyLinearImpulse(0, 3, pos.x, pos.y, true);
                System.out.println("jump, " + characterBody.getLinearVelocity());
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
        this.jump = true;
    }

    public void moveLeft() {
        if (isFacingRight) {
            flipAnimations();
            isFacingRight = false;
        }
    }

    public void moveRight() {
        if (!isFacingRight) {
            flipAnimations();
            isFacingRight = true;
        }
    }

    public Vector2 getPosition() {
        return characterBody.getPosition();
    }

    public Fixture getSensorFixture() {
        return characterPhysicsFixture;
    }

    public void setGrounded(boolean grounded) {
        //Gdx.app.log("Character", grounded ? "Character is grounded" : "Character is not grounded");
        this.grounded = grounded;
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
