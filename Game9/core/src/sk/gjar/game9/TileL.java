package sk.gjar.game9;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class TileL extends Tile {
    public TileL(World world, float positionX, float positionY) {
        super(world, positionX, positionY);
        sprite.setTexture(new Texture("13.png"));
    }
}
