package sk.gjar.game9;

import com.badlogic.gdx.physics.box2d.World;

public class TileC extends Tile {
    public TileC(World world, float positionX, float positionY) {
        super(world, positionX, positionY);
    }

    @Override
    public String getTextureId() {
        return "14.png";
    }
}
