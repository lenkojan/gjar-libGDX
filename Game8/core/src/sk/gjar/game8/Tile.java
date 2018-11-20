package sk.gjar.game8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class Tile {
    public static final float WIDTH = 1f;
    public static final float HEIGHT = 1f;
    private final Sprite sprite;

    public Tile(World world, float positionX, float positionY) {
        this.sprite = new Sprite(new Texture("2.png"));
        this.sprite.setPosition(positionX, positionY);
        this.sprite.setSize(WIDTH, HEIGHT);
    }

    public void draw(SpriteBatch spriteBatch) {
        this.sprite.draw(spriteBatch);
    }
}
