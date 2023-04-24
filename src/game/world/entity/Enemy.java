package game.world.entity;

import game.renderer.Texture;
import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public class Enemy extends Entity {

    Texture image;

    public Enemy(World world, Vec2f location, Vec2f velocity, float height, Vec2f size, int sector, Texture image) {
        super(world, location, velocity, height, size, sector);
        this.image = image;
    }

    @Override
    public Texture getTexture() {
        return image;
    }
}
