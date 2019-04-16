package sk.gjar.game11;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Eatables {
    private static final float TILE_SIZE_HEIGHT = 0.4f;
    private static final float TILE_SIZE_WIDTH = 0.02f;
    private final World world;
    private final Body eatableBody;
    private final int x;
    private final int y;

    public Eatables(int x, int y, World world) {
        this.world = world;
        this.x = x;
        this.y = y;
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(x + (0.4f / 2f - TILE_SIZE_WIDTH / 2), y + TILE_SIZE_HEIGHT / 2f);
        eatableBody = world.createBody(groundBodyDef);
        eatableBody.setUserData(this);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(TILE_SIZE_WIDTH / 2f, TILE_SIZE_HEIGHT / 2f);
        eatableBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void consume() {
        world.destroyBody(eatableBody);
    }
}
