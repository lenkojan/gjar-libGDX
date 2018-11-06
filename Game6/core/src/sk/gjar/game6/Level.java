package sk.gjar.game6;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Level {

    private final int[] platform;
    private final Texture[] textures;
    public static final int TILE_WIDTH = 128;
    public static final int TILE_HEIGHT = 128;

    public Level() {
        platform = new int[]{-1, 0, 1, 1, 1, 1, 1, 1, 2, -1};
        textures = new Texture[5];
        textures[0] = new Texture("1.png");
        textures[1] = new Texture("2.png");
        textures[2] = new Texture("3.png");
    }

    public void update(float deltaTime) {

    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < platform.length; i++) {
            if (platform[i] > -1)
                batch.draw(textures[platform[i]], TILE_WIDTH * i, 0);
        }
    }

    public void dispose() {
        for (int i = 0; i < textures.length; i++) {
            textures[i].dispose();
        }
    }
}
