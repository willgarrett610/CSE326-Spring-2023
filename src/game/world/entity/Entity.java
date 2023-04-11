package game.world.entity;

import game.renderer.Texture;
import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public abstract class Entity {

    private World world;
    private Vec2f location;
    private float height;
    private Vec2f size;
    private int sector;

    public Entity(World world, Vec2f location, float height, Vec2f size, int sector) {
        this.world = world;
        this.location = location;
        this.height = height;
        this.size = size;
        this.sector = sector;
    }

    public abstract Texture getTexture();

    public World getWorld() {
        return this.world;
    }

    public Vec2f getLocation() {
        return this.location;
    }

    public float getHeight() {
        return this.height;
    }

    public Vec2f getSize() {
        return this.size;
    }

    public int getSector() {
        return this.sector;
    }

    public void setLocation() {
        this.location = location;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

}
