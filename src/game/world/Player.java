package game.world;

public class Player extends Moveable{
    public float angle;

    public Player(World world) {
        super(world, world.startLocation, world.startSector);
        this.angle = world.startAngle;
    }

    public void setWorld(World world) {
        this.world = world;
        this.location = world.startLocation;
        this.sector = world.startSector;
        this.angle = world.startAngle;
    }
}
