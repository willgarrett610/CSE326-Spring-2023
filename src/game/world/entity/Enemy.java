package game.world.entity;

import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public class Enemy extends Entity {

    BufferedImage image;

    public Enemy(World world, Vec2f location, float height, int sector, BufferedImage image) {
        super(world, location, height, sector);
        this.image = image;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}
