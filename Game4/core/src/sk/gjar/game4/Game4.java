package sk.gjar.game4;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.Rectangle;

public class Game4 extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture bg;
    int[] platform;
    Texture[] textures;
    Sprite zombie;
    Sprite mushroom;
    static final int TILE_WIDTH = 128;
    static final int TILE_HEIGHT = 128;
    static final int SPEED = 220;
    boolean facingRight;
    boolean isMushroomEaten;
    Rectangle zombieBounds;

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
        zombie = new Sprite(textures[4]);
        zombie.setPosition(400, TILE_HEIGHT - 8);
        mushroom = new Sprite(textures[3]);
        mushroom.setPosition(600, TILE_HEIGHT - 6);
        facingRight = true;
        isMushroomEaten = false;
        zombieBounds = new Rectangle(200, 100, 880, 600);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0, 1280, 768);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (facingRight) {
                facingRight = false;
                zombie.flip(true, false);
            }
            float newZombiePosition = zombie.getX() - SPEED * Gdx.graphics.getDeltaTime();
            if (newZombiePosition > zombieBounds.x)
                zombie.setPosition(newZombiePosition, zombie.getY());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!facingRight) {
                facingRight = true;
                zombie.flip(true, false);
            }
            float newZombiePosition = zombie.getX() + SPEED * Gdx.graphics.getDeltaTime();
            if (newZombiePosition + zombie.getBoundingRectangle().getWidth() < zombieBounds.x + zombieBounds.width)
                zombie.setPosition(newZombiePosition, zombie.getY());
        }
        if (!isMushroomEaten && zombie.getBoundingRectangle().contains(mushroom.getBoundingRectangle()))
            isMushroomEaten = true;
        for (int i = 0; i < platform.length; i++) {
            if (platform[i] > -1)
                batch.draw(textures[platform[i]], TILE_WIDTH * i, 0);
        }
        if (!isMushroomEaten)
            mushroom.draw(batch);
        zombie.draw(batch);
        batch.end();
        shapeRenderer.begin();
        shapeRenderer.rect(zombie.getBoundingRectangle().x, zombie.getBoundingRectangle().y, zombie.getBoundingRectangle().width, zombie.getBoundingRectangle().height);
        shapeRenderer.rect(mushroom.getBoundingRectangle().x, mushroom.getBoundingRectangle().y, mushroom.getBoundingRectangle().width, mushroom.getBoundingRectangle().height);
        shapeRenderer.rect(zombieBounds.x, zombieBounds.y, zombieBounds.width, zombieBounds.height);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bg.dispose();
    }
}
