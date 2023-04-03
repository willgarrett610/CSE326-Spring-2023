package game.world.entity;

import game.renderer.Texture;
import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public class Enemy extends Entity {

    Texture image;

    public Enemy(World world, Vec2f location, float height, int sector, Texture image) {
        super(world, location, height, sector);
        this.image = image;
    }

    @Override
    public Texture getTexture() {
        return image;
    }
}
