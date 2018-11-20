package sk.gjar.game8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Game8 extends ApplicationAdapter {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private SpriteBatch spriteBatch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Level level;
    private Character character;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(6 * (w / h), 6);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        spriteBatch = new SpriteBatch();
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        level = new Level(world, camera.viewportWidth, camera.viewportHeight);
        character = new Character(world, camera.viewportWidth, camera.viewportHeight);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        float deltaTime = Gdx.graphics.getDeltaTime();
        handleUserInput();
        world.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        character.update(deltaTime);
        spriteBatch.begin();
        //level.draw(spriteBatch);
        character.draw(spriteBatch);
        spriteBatch.end();
        debugRenderer.render(world, camera.combined);
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

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
