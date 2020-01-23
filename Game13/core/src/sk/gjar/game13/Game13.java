package sk.gjar.game13;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game13 extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    int score = 0;
    float timeout = 0f;
    float MAX_TIMEOUT = 1f;
    private GameListener listener;

    public Game13(GameListener listener) {
        super();
        this.listener = listener;
    }

    public Game13() {
        super();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        if (listener != null && timeout < 0) {
            timeout = MAX_TIMEOUT;
            score++;
            listener.updateScore(score);
        }
        timeout -= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
