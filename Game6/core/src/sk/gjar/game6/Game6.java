package sk.gjar.game6;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.swing.text.MaskFormatter;

public class Game6 extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture bg;
    private Level level;
    private Character character;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        bg = new Texture("BG.png");
        level = new Level();
        character = new Character();
        Mushroom a = new Mushroom();
        a.mushroom.get
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        level.update(deltaTime);
        character.update(deltaTime);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character.handleAction(Character.CharacterAction.MoveLeft);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character.handleAction(Character.CharacterAction.MoveRight);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            character.handleAction(Character.CharacterAction.Jump);
        }
        batch.begin();
        batch.draw(bg, 0, 0, 1280, 768);
        level.draw(batch);
        character.draw(batch);
        batch.end();
        shapeRenderer.begin();
        character.drawBounds(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bg.dispose();
        character.dispose();
        level.dispose();
    }
}
