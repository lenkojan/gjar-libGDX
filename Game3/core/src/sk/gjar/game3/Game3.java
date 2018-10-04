package sk.gjar.game3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game3 extends ApplicationAdapter {
    SpriteBatch batch;
    Texture platformLeft;
    Texture platformCenter;
    Texture platformRight;
    static final int WIDTH = 10;
    static final int TEXTURE_WIDTH = 128;
    /* solution 1
    Texture[] bottomLine;
    */
    /* solution 2
    int[] bottomLine;
    */
    /* solution 3
    int[] bottomLine;
    Texture[] textures;
    */
    /* zombie texture
    Texture zombieTexture;
    */
    /* zombie sprite
    Sprite zombieSprite;
    */


    @Override
    public void create() {
        batch = new SpriteBatch();
        platformLeft = new Texture("1.png");
        platformCenter = new Texture("2.png");
        platformRight = new Texture("3.png");
        /* zombie texture
        zombieTexture = new Texture("Idle (1).png");
        */
        /* zombie sprite
        zombieSprite = new Sprite(zombieTexture);
        zombieSprite.setScale(0.3f);
        zombieSprite.setPosition(400, -60);
        */
        /* solution 1
        bottomLine = new Texture[WIDTH];
        bottomLine[0] = null;
        bottomLine[1] = platformLeft;
        bottomLine[2] = platformCenter;
        bottomLine[3] = platformCenter;
        bottomLine[4] = platformCenter;
        bottomLine[5] = platformCenter;
        bottomLine[6] = platformCenter;
        bottomLine[7] = platformCenter;
        bottomLine[8] = platformRight;
        bottomLine[9] = null;
        */

        /* solution 2
        bottomLine = new int[]{ 0, 1, 2, 2,2,2, 2, 2, 3, 0};
        */

        /* solution 3
        textures = new Texture[]{platformLeft, platformCenter, platformRight};
        bottomLine = new int[]{0, 0, 1, 2, 2, 2, 2, 3, 0, 0};
        */

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(135f/255f, 206f/255f, 235f/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        /* solution 1
        for (int i = 0; i < bottomLine.length; i++) {
            if (bottomLine[i] != null) {
                batch.draw(bottomLine[i],
                        i * TEXTURE_WIDTH,
                        0);
            }
        }
        */

        /* solution 2
        for (int i = 0; i < bottomLine.length; i++) {
            if (bottomLine[i] == 1) {
                batch.draw(platformLeft,
                        i * TEXTURE_WIDTH,
                        0);
            } else if (bottomLine[i] == 2) {
                batch.draw(platformCenter,
                        i * TEXTURE_WIDTH,
                        0);
            } else if (bottomLine[i] == 3) {
                batch.draw(platformRight,
                        i * TEXTURE_WIDTH,
                        0);
            }
        }
        */

        /* solution 3
        for (int i = 0; i < bottomLine.length; i++) {
            if (bottomLine[i] > 0) {
                batch.draw(textures[bottomLine[i] - 1],
                        i * TEXTURE_WIDTH,
                        0);
            }
        }
        */

        /* zombie texture
        batch.draw(zombieTexture, 400, 110);
        */

        /* zombie sprite
        zombieSprite.draw(batch);
        */
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        platformLeft.dispose();
        platformCenter.dispose();
        platformRight.dispose();
        /* disposing zombie texture
        zombieTexture.dispose();
        */
    }
}
