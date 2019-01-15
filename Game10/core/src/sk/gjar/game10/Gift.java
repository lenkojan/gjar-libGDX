package sk.gjar.game10;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Gift {
    public static final float WIDTH = 0.3f;
    public static final float HEIGHT = 0.3f;
    private final float finalPositionX;
    private final float finalPositionY;
    private final Body body;
    public Sprite sprite;
    private boolean isDrawn;

    public Gift(World world, float positionX, float positionY, float finalPositionX, float finalPositionY) {
        this.sprite = new Sprite(new Texture("package.png"));
        this.sprite.setSize(WIDTH, HEIGHT);
        this.sprite.setPosition(positionX, positionY);
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(positionX + WIDTH / 2, positionY + HEIGHT / 2);
        body = world.createBody(groundBodyDef);
        body.setUserData(this);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(WIDTH / 4, HEIGHT / 2);
        body.createFixture(groundBox, 0.0f);
        groundBox.dispose();
        this.finalPositionX = finalPositionX;
        this.finalPositionY = finalPositionY;
        isDrawn = true;
    }

    public void consume(World world) {
        isDrawn = false;
        world.destroyBody(body);
        sprite.setPosition(finalPositionX, finalPositionY);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (isDrawn)
            this.sprite.draw(spriteBatch);
    }

    public void show() {
        isDrawn = true;
    }

}
