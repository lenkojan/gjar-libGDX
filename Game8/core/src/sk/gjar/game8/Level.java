package sk.gjar.game8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Level {
    private final float width;
    private final float height;
    private final Texture bg;
    Array<Tile> tiles;

    public Level(World world, float width, float height) {
        this.width = width;
        this.height = height;
        this.bg = new Texture("BG.png");
        this.tiles = new Array<Tile>();
        float usedWidth = 0;
        while (usedWidth < width) {
            this.tiles.add(new Tile(world, usedWidth, 0));
            usedWidth += Tile.WIDTH;
        }
        //ground
        BodyDef groundBodyDef = new BodyDef();
        Body groundBody = world.createBody(groundBodyDef);
        EdgeShape groundEdge = new EdgeShape();
        groundEdge.set(0, Tile.HEIGHT, width, Tile.HEIGHT);
        groundBody.createFixture(groundEdge, 0.0f);
        groundEdge.dispose();
        //left
        BodyDef leftBodyDef = new BodyDef();
        Body leftBody = world.createBody(leftBodyDef);
        EdgeShape leftEdge = new EdgeShape();
        leftEdge.set(0, 0, 0, height);
        leftBody.createFixture(leftEdge, 0.0f);
        leftEdge.dispose();
        //right
        BodyDef rightBodyDef = new BodyDef();
        Body rightBody = world.createBody(rightBodyDef);
        EdgeShape rightEdge = new EdgeShape();
        rightEdge.set(width, 0, width, height);
        rightBody.createFixture(rightEdge, 0.0f);
        rightEdge.dispose();
        //top
        BodyDef topBodyDef = new BodyDef();
        Body topBody = world.createBody(topBodyDef);
        EdgeShape topEdge = new EdgeShape();
        topEdge.set(0, height, width, height);
        topBody.createFixture(topEdge, 0.0f);
        topEdge.dispose();
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(this.bg, 0, 0, this.width, this.height);
        for (Tile tile : this.tiles) {
            tile.draw(spriteBatch);
        }
    }
}
