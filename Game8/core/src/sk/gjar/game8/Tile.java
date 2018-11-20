package sk.gjar.game8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Tile {
    public static final float WIDTH = 1f;
    public static final float HEIGHT = 1f;
    private final Sprite sprite;

    public Tile(World world, float positionX, float positionY, String textureName) {
        this.sprite = new Sprite(new Texture(textureName));
        this.sprite.setPosition(positionX, positionY);
        this.sprite.setSize(WIDTH, HEIGHT);
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(positionX + WIDTH / 2, positionY + HEIGHT / 2);
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(WIDTH / 2, HEIGHT / 2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

    }

    public void draw(SpriteBatch spriteBatch) {
        this.sprite.draw(spriteBatch);
    }
}
