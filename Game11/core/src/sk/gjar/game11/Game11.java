package sk.gjar.game11;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;

public class Game11 extends ApplicationAdapter {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    SpriteBatch spriteBatch;
    SpriteBatch fontBatch;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    private Sprite bg;
    private float cameraWidth;
    private float worldWidth = 36;
    private float worldHeight = 10;
    private float cameraHeight;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Level level;
    private Character character;
    private BitmapFont font;
    private int score = 0;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        fontBatch = new SpriteBatch();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cameraHeight = 6;
        cameraWidth = cameraHeight * (w / h);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraWidth, cameraHeight);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                if (isFalsePositive(contact, oldManifold))
                    contact.setEnabled(false);
                Eatables eatable = checkConsumtion(contact);
                if (eatable != null) {
                    contact.setEnabled(false);
                    level.addConsumption(eatable);
                    score++;
                }

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        debugRenderer = new Box2DDebugRenderer();
        level = new Level(world, worldWidth, worldHeight);
        character = new Character(world);
        font = new BitmapFont(Gdx.files.internal("fnt.fnt"),
                Gdx.files.internal("fnt.png"), false);
        //font.getData().setScale(8f / Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        handleUserInput();
        float deltaTime = Gdx.graphics.getDeltaTime();
        world.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        //checkForPackages();
        //checkForTree();
        character.setGrounded(isPlayerGrounded());
        character.update(deltaTime);
        updateCamera();
        level.draw(spriteBatch, camera);
        spriteBatch.begin();
        character.draw(spriteBatch);
        spriteBatch.end();
        fontBatch.begin();
        font.setColor(0f, 0f, 0f, 1.0f);
        if (score > 100)
            font.draw(fontBatch, "Score : " + String.valueOf(score), 50, Gdx.graphics.getHeight() - 20);
        else if (score > 10)
            font.draw(fontBatch, "Score :  " + String.valueOf(score), 50, Gdx.graphics.getHeight() - 20);
        else
            font.draw(fontBatch, "Score :   " + String.valueOf(score), 50, Gdx.graphics.getHeight() - 20);
        fontBatch.end();
        debugRenderer.render(world, camera.combined);
        level.updateConsumption();
    }

    private Eatables checkConsumtion(Contact contact) {
        Fixture characterFixture = null;
        Fixture eatableFixture = null;
        if (contact.getFixtureA() == character.getSensorFixture()) {
            characterFixture = contact.getFixtureA();
            if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof Eatables) {
                eatableFixture = contact.getFixtureB();
            }
        } else if (contact.getFixtureB() == character.getSensorFixture()) {
            characterFixture = contact.getFixtureB();
            if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof Eatables) {
                eatableFixture = contact.getFixtureA();
            }
        }
        if (contact.isTouching() && (characterFixture != null && eatableFixture != null)) {
            return (Eatables) eatableFixture.getBody().getUserData();
        }
        return null;
    }

    private boolean isFalsePositive(Contact contact, Manifold oldManifold) {
        Fixture characterFixture = null;
        Fixture tileFixture = null;
        if (contact.getFixtureA() == character.getSensorFixture()) {
            characterFixture = contact.getFixtureA();
            if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof Tile) {
                tileFixture = contact.getFixtureB();
            }
        } else if (contact.getFixtureB() == character.getSensorFixture()) {
            characterFixture = contact.getFixtureB();
            if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof Tile) {
                tileFixture = contact.getFixtureA();
            }
        }
        if (contact.isTouching() && (characterFixture != null && tileFixture != null)) {
            float tileTop = ((Tile) tileFixture.getBody().getUserData()).y + Level.TILE_SIZE;
            WorldManifold manifold = contact.getWorldManifold();
            int count = 0;
            float contactX = -1;
            for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                if (manifold.getPoints()[j].y - 0.021f < tileTop && manifold.getPoints()[j].y > tileTop) {
                    if (contactX != -1 && contactX != manifold.getPoints()[j].x) {
                        return false;
                    } else {
                        contactX = manifold.getPoints()[j].x;
                    }
                    count++;
                }
            }

            if (count == manifold.getPoints().length) {
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerGrounded() {
        Array<Contact> contactList = world.getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if (contact.isTouching() && (contact.getFixtureA() == character.getSensorFixture() ||
                    contact.getFixtureB() == character.getSensorFixture())) {
                Vector2 pos = character.getPosition();
                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                    below &= (manifold.getPoints()[j].y < pos.y - 0.001f);
                }

                if (below) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void handleUserInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character.moveRight();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            character.jump();
        }
    }

    private void updateCamera() {
        if (camera.position.x > cameraWidth / 2) {
            if (character.getPosition().x < camera.position.x) {
                camera.position.x = character.getPosition().x;
            }
        }
        if (camera.position.x < worldWidth - cameraWidth / 2) {
            if (character.getPosition().x > camera.position.x) {
                camera.position.x = character.getPosition().x;
            }
        }
        if (camera.position.x < cameraWidth / 2) {
            camera.position.x = cameraWidth / 2;
        }
        if (camera.position.x > worldWidth - cameraWidth / 2) {
            camera.position.x = worldWidth - cameraWidth / 2;
        }
        if (camera.position.y > cameraHeight / 2) {
            if (character.getPosition().y < camera.position.y) {
                camera.position.y = character.getPosition().y;
            }
        }
        if (camera.position.y < worldHeight - cameraHeight / 2) {
            if (character.getPosition().y > camera.position.y) {
                camera.position.y = character.getPosition().y;
            }
        }
        if (camera.position.y < cameraHeight / 2) {
            camera.position.y = cameraHeight / 2;
        }
        if (camera.position.y > worldHeight - cameraHeight / 2) {
            camera.position.y = worldHeight - cameraHeight / 2;
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
