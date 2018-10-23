package sk.gjar.game5;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Game5 extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture bg;
    int[] platform;
    Texture[] textures;
    Sprite mushroom;
    static final int TILE_WIDTH = 128;
    static final int TILE_HEIGHT = 128;
    static final int SPEED = 220;
    boolean facingRight;
    boolean isMushroomEaten;
    Rectangle acitivityBounds;
    Rectangle zombieBounds;
    float stateTime;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> walkAnimation;
    Animation<TextureRegion> attackAnimation;
    TextureAtlas textureAtlas;
    int zombieState = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        bg = new Texture("BG.png");
        platform = new int[]{-1, 0, 1, 1, 1, 1, 1, 1, 2, -1};
        textures = new Texture[5];
        textures[0] = new Texture("1.png");
        textures[1] = new Texture("2.png");
        textures[2] = new Texture("3.png");
        textures[3] = new Texture("mushroom.png");
        textures[4] = new Texture("zombie.png");
        mushroom = new Sprite(textures[3]);
        mushroom.setPosition(MathUtils.random(200) + 500, TILE_HEIGHT - 6);
        facingRight = true;
        isMushroomEaten = false;
        acitivityBounds = new Rectangle(200, 100, 880, 600);
        zombieBounds = new Rectangle(200, TILE_HEIGHT - 8, 120, 150);
        textureAtlas = new TextureAtlas("zombie.atlas");
        idleAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        walkAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Walk"), Animation.PlayMode.LOOP);
        attackAnimation =
                new Animation<TextureRegion>(0.07f, textureAtlas.findRegions("Attack"), Animation.PlayMode.NORMAL);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        // Get current frame of animation for the current stateTime
        batch.begin();
        batch.draw(bg, 0, 0, 1280, 768);
        if (attackAnimation.isAnimationFinished(stateTime) && zombieState == 2) {
            mushroom.setX(MathUtils.random(200, 800));
            zombieState = 0;
        }
        for (int i = 0; i < platform.length; i++) {
            if (platform[i] > -1)
                batch.draw(textures[platform[i]], TILE_WIDTH * i, 0);
        }
        if (zombieState != 2) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (facingRight) {
                    facingRight = false;
                    flipAnimations();
                }
                float newZombiePosition = zombieBounds.x - SPEED * Gdx.graphics.getDeltaTime();
                if (newZombiePosition > acitivityBounds.x) {
                    zombieBounds.setX(newZombiePosition);
                    if (zombieState != 1)
                        stateTime = 0;
                    zombieState = 1;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (!facingRight) {
                    facingRight = true;
                    flipAnimations();
                }
                float newZombiePosition = zombieBounds.x + SPEED * Gdx.graphics.getDeltaTime();
                if (newZombiePosition + 130 < acitivityBounds.x + acitivityBounds.width) {
                    zombieBounds.setX(newZombiePosition);
                    if (zombieState != 1)
                        stateTime = 0;
                    zombieState = 1;
                }
            } else {
                if (zombieState != 0)
                    stateTime = 0;
                zombieState = 0;
            }
        }
        if (zombieBounds.contains(mushroom.getBoundingRectangle()) && !isMushroomEaten) {
            Gdx.app.debug("Zombie attack", "Zombie should attack");
            if (zombieState != 2) {
                stateTime = 0;
                zombieState = 2;
            }
        }
        if (!isMushroomEaten)
            mushroom.draw(batch);

        TextureRegion currentFrame;
        switch (zombieState) {
            case 2:
                currentFrame = attackAnimation.getKeyFrame(stateTime, true);
                break;
            case 1:
                currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                break;
            default:
                currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }
        batch.draw(currentFrame, zombieBounds.x, zombieBounds.y); // Draw current frame at (50, 50)
        batch.end();
        shapeRenderer.begin();
        shapeRenderer.rect(mushroom.getBoundingRectangle().x, mushroom.getBoundingRectangle().y, mushroom.getBoundingRectangle().width, mushroom.getBoundingRectangle().height);
        shapeRenderer.rect(acitivityBounds.x, acitivityBounds.y, acitivityBounds.width, acitivityBounds.height);
        shapeRenderer.rect(zombieBounds.x, zombieBounds.y, zombieBounds.width, zombieBounds.height);
        shapeRenderer.end();
    }

    private void flipAnimations() {
        for (int i = 0; i < idleAnimation.getKeyFrames().length; i++) {
            idleAnimation.getKeyFrames()[i].flip(true, false);
        }
        for (int i = 0; i < walkAnimation.getKeyFrames().length; i++) {
            walkAnimation.getKeyFrames()[i].flip(true, false);
        }for (int i = 0; i < attackAnimation.getKeyFrames().length; i++) {
            attackAnimation.getKeyFrames()[i].flip(true, false);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
