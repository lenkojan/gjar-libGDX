package sk.gjar.game7;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Game7 extends ApplicationAdapter {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private SpriteBatch spriteBatch;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    static final float BALL_WIDTH = 1f;
    static final float BALL_HEIGHT = 1f;

    private OrthographicCamera camera;

    private Sprite ball;
    private Body ballBody;


    @Override
    public void create() {
        ball = new Sprite(new Texture(Gdx.files.internal("ball.png")));
        ball.setSize(BALL_WIDTH, BALL_HEIGHT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(6 * (w / h), 6);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        spriteBatch = new SpriteBatch();
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        createBallObject();
        createBottomObject();
        createLeftObject();
        createRightObject();
    }

    private void createBottomObject() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(camera.viewportWidth / 2, 0.1f));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth / 2, 0.1f);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void createLeftObject() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0.1f, camera.viewportHeight / 2));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(0.1f, camera.viewportHeight / 2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void createRightObject() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(camera.viewportWidth - 0.1f, camera.viewportHeight / 2));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(0.1f, camera.viewportHeight / 2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void createBallObject() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(camera.viewportWidth / 2, 6);
        ballBody = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(BALL_WIDTH / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        Fixture fixture = ballBody.createFixture(fixtureDef);
        circle.dispose();
    }


    @Override
    public void render() {
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleUserInput();
        world.step(Gdx.graphics.getDeltaTime(), VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        ball.setCenter(ballBody.getPosition().x, ballBody.getPosition().y);
        spriteBatch.begin();
        ball.draw(spriteBatch);
        spriteBatch.end();
        debugRenderer.render(world, camera.combined);
    }

    private void handleUserInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            ballBody.applyForceToCenter(-10f, 0, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            ballBody.applyForceToCenter(10f, 0, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (ballBody.getLinearVelocity().y == 0)
                ballBody.applyForceToCenter(0, 100f, true);
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
