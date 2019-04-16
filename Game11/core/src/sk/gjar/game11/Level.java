package sk.gjar.game11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Level {
    public static final float TILE_SIZE = 1f;
    private static final int GROUND_ID = 1;
    private final float width;
    private final float height;
    private final Sprite bg;
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final TiledMapTileLayer waterLayer;
    private final TiledMapTileLayer tilesLayer;
    private final TiledMapTileLayer eatablesLayer;
    private final int[] decorationLayersIndices;
    private Array<Eatables> toConsume;

    public Level(World world, float width, float height) {
        this.width = width;
        this.height = height;
        toConsume = new Array<Eatables>();
        tiledMap = new TmxMapLoader().load("l0.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / 128f);

        MapLayers mapLayers = tiledMap.getLayers();
        waterLayer = (TiledMapTileLayer) mapLayers.get("water");
        tilesLayer = (TiledMapTileLayer) mapLayers.get("tiles");
        eatablesLayer = (TiledMapTileLayer) mapLayers.get("eatables");

        bg = new Sprite(new Texture("freetileset/png/BG/BG.png"));
        bg.setSize(10 * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 10);

        decorationLayersIndices = new int[]{
                mapLayers.getIndex("nonMovable")
        };
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 36; j++) {
                if (tilesLayer.getCell(j, i) != null) {
                    BodyDef groundBodyDef = new BodyDef();
                    groundBodyDef.position.set(j + TILE_SIZE / 2f, i + TILE_SIZE / 2f);
                    Body groundBody = world.createBody(groundBodyDef);
                    groundBody.setUserData(new Tile(j, i));
                    PolygonShape groundBox = new PolygonShape();
                    groundBox.setAsBox(TILE_SIZE / 2f, TILE_SIZE / 2f);
                    groundBody.createFixture(groundBox, 0.0f);
                    groundBox.dispose();
                }
                if (eatablesLayer.getCell(j, i) != null) {
                    new Eatables(j, i, world);
                }
            }
        }

        //ground
        BodyDef groundBodyDef = new BodyDef();
        Body groundBody = world.createBody(groundBodyDef);
        groundBody.setUserData(GROUND_ID);
        EdgeShape groundEdge = new EdgeShape();
        groundEdge.set(0, 0, width, 0);
        groundBody.createFixture(groundEdge, 0.0f);
        groundEdge.dispose();
        //left
        BodyDef leftBodyDef = new BodyDef();
        Body leftBody = world.createBody(leftBodyDef);
        EdgeShape leftEdge = new EdgeShape();
        leftEdge.set(0, 0, 0, height * 6);
        leftBody.createFixture(leftEdge, 0.0f);
        leftEdge.dispose();
        //right
        BodyDef rightBodyDef = new BodyDef();
        Body rightBody = world.createBody(rightBodyDef);
        EdgeShape rightEdge = new EdgeShape();
        rightEdge.set(width, 0, width, height * 6);
        rightBody.createFixture(rightEdge, 0.0f);
        rightEdge.dispose();
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        batch.begin();
        batch.draw(bg, camera.position.x - camera.viewportWidth / 2 - 0.1f, 0, camera.viewportWidth + 0.2f, 10);
        batch.end();
        tiledMapRenderer.setView(camera);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(waterLayer);
        tiledMapRenderer.renderTileLayer(tilesLayer);
        tiledMapRenderer.getBatch().end();
        tiledMapRenderer.render(decorationLayersIndices);
        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(eatablesLayer);
        tiledMapRenderer.getBatch().end();

    }

    public void consume(Eatables eatable) {
        eatablesLayer.setCell(eatable.getX(), eatable.getY(), null);
        eatable.consume();
    }

    public void addConsumption(Eatables eatable) {
        toConsume.add(eatable);
    }

    public void updateConsumption() {
        for (Eatables e : toConsume) {
            consume(e);
        }
        toConsume.clear();
    }
}
