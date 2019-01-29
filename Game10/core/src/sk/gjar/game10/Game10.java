package sk.gjar.game10;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;

public class Game10 extends ApplicationAdapter {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private SpriteBatch spriteBatch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Level level;
    private Character character;
    private float cameraWidth;
    private float worldWidth;
    private float worldHeight;
    private float cameraHeight;
    private Sprite introImage;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cameraHeight = 6;
        cameraWidth = cameraHeight * (w / h);

        worldWidth = cameraWidth * 3;
        worldHeight = cameraHeight * 2;
        camera = new OrthographicCamera(cameraWidth, cameraHeight);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        spriteBatch = new SpriteBatch();
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        level = new Level(world, worldWidth, worldHeight);
        character = new Character(world);
        introImage = new Sprite(new Texture("intro.jpg"));
        introImage.setSize(cameraWidth, cameraHeight);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        handleUserInput();
        if (introImage != null) {
            spriteBatch.begin();
            introImage.draw(spriteBatch);
            spriteBatch.end();
        } else {
            float deltaTime = Gdx.graphics.getDeltaTime();
            world.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            checkForPackages();
            checkForTree();
            character.setGrounded(isPlayerGrounded(Gdx.graphics.getDeltaTime()));
            character.update(deltaTime);
            updateCamera();
            spriteBatch.begin();
            level.draw(spriteBatch);
            character.draw(spriteBatch);
            spriteBatch.end();
            debugRenderer.render(world, camera.combined);
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

    private boolean isPlayerGrounded(float deltaTime) {
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

    private void checkForPackages() {
        Array<Contact> contactList = world.getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if (contact.getFixtureA().getBody().getUserData() instanceof Gift) {
                ((Gift) contact.getFixtureA().getBody().getUserData()).consume(world);
            } else if (contact.getFixtureB().getBody().getUserData() instanceof Gift) {
                ((Gift) contact.getFixtureB().getBody().getUserData()).consume(world);
            }
        }
    }

    private void checkForTree() {
        Array<Contact> contactList = world.getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if (contact.getFixtureA().getBody().getUserData() instanceof Tree) {
                ((Tree) contact.getFixtureA().getBody().getUserData()).consume(world);
                level.showGifts();
            } else if (contact.getFixtureB().getBody().getUserData() instanceof Tree) {
                ((Tree) contact.getFixtureB().getBody().getUserData()).consume(world);
                level.showGifts();
            }
        }
    }

    private void handleUserInput() {
        if (introImage != null && Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            introImage = null;
        } else {
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
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
