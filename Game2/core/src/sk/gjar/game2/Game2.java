package sk.gjar.game2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game2 extends ApplicationAdapter {
    SpriteBatch batch;
    static final int WIDTH = 10;
    static final int HEIGHT = 6;
    Texture bg;
    static final int[][] template = new int[][]{
            {0, 1, 2, 2, 2, 2, 2, 2, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 13, 14, 14, 15, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
    static final int[][] templateBG = new int[][]{
            {17, 17, 0, 0, 0, 0, 0, 0, 17, 17},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
    Texture[] textures;
    Sprite background;

    @Override
    public void create() {
        batch = new SpriteBatch();
        bg = new Texture("18.png");
        textures = new Texture[19];
        textures[1] = new Texture("1.png");
        textures[2] = new Texture("2.png");
        textures[3] = new Texture("3.png");
        textures[13] = new Texture("13.png");
        textures[14] = new Texture("14.png");
        textures[15] = new Texture("15.png");
        textures[17] = new Texture("17.png");
        textures[18] = new Texture("18.png");
        background = new Sprite(new Texture("BG.png"));
        background.setSize(WIDTH * bg.getWidth(), HEIGHT * bg.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                batch.draw(bg, i * bg.getWidth(), j * bg.getHeight());
            }
        }
        background.draw(batch);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (templateBG[j][i] != 0)
                    batch.draw(textures[templateBG[j][i]], i * bg.getWidth(), j * bg.getHeight());
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
