package sk.gjar.game9;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Tree {

    public static final float WIDTH = 1f;
    public static final float HEIGHT = 1f;
    private final Body treeBody;
    public Sprite sprite;

    public Tree(World world, float positionX, float positionY) {
        this.sprite = new Sprite(new Texture("tree.png"));
        this.sprite.setPosition(positionX, positionY);
        this.sprite.setSize(WIDTH, HEIGHT);
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(positionX + WIDTH / 2, positionY + HEIGHT / 2);
        treeBody = world.createBody(groundBodyDef);
        treeBody.setUserData(this);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(WIDTH / 4, HEIGHT / 2);
        treeBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

    }

    public void draw(SpriteBatch spriteBatch) {
        this.sprite.draw(spriteBatch);
    }

    public void consume(World world) {
        world.destroyBody(treeBody);
    }
}
