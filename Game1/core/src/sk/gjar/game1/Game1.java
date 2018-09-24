package sk.gjar.game1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game1 extends ApplicationAdapter {
    SpriteBatch batch;
    Texture platformLeft;
    Texture platformCenter;
    Texture platformRight;
    static final int CENTER_TILES_COUNT = 2;

    @Override
    public void create() {
        batch = new SpriteBatch();
        platformLeft = new Texture("1.png");
        platformCenter = new Texture("2.png");
        platformRight = new Texture("3.png");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(135f/255f, 206f/255f, 235f/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(platformLeft, 0, 0);
        for (int i = 0; i < CENTER_TILES_COUNT; i++) {
            batch.draw(platformCenter, platformLeft.getWidth() + i * platformCenter.getWidth(), 0);
        }
        batch.draw(platformRight, platformLeft.getWidth() + platformCenter.getWidth() * CENTER_TILES_COUNT, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        platformLeft.dispose();
        platformCenter.dispose();
        platformRight.dispose();
    }
}
