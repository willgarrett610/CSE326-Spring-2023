package game.world.entity;

import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;

public abstract class Entity {

    private World world;
    private Vec2f location;
    private float height;
    private int sector;

    public Entity(World world, Vec2f location, float height, int sector) {
        this.world = world;
        this.location = location;
        this.height = height;
        this.sector = sector;
    }

    public abstract BufferedImage getImage();

    public World getWorld() {
        return this.world;
    }

    public Vec2f getLocation() {
        return this.location;
    }

    public float getHeight() {
        return this.height;
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
