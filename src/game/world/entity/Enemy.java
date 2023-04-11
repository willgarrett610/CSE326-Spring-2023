package game.world.entity;

import game.renderer.Texture;
import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public class Enemy extends Entity {

    Texture image;

    public Enemy(World world, Vec2f location, float height, Vec2f size, int sector, Texture image) {
        super(world, location, height, size, sector);
        this.image = image;
    }

    @Override
    public Texture getTexture() {
        return image;
    }
}
