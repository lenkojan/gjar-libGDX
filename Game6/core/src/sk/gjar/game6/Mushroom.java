package sk.gjar.game6;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Mushroom {
    private final Sprite mushroom;
    private final boolean isMushroomEaten;

    public Mushroom() {
        mushroom = new Sprite(new Texture("mushroom.png"));
        mushroom.setPosition(MathUtils.random(200) + 500, Level.TILE_HEIGHT - 6);
        isMushroomEaten = false;
    }

    public void draw(SpriteBatch spriteBatch) {
        if (!isMushroomEaten) {
            mushroom.draw(spriteBatch);
        }
    }
}
