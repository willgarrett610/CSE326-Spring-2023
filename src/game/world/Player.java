package game.world;

public class Player {

    public World world;
    public Vec2f location;
    public float angle;
    public int sector;

    public Player(World world) {
        setWorld(world);
    }

    public void move(Vec2f moveTo) {
        Sector sector = world.sectors.get(this.sector);
        int[] sectVerts = sector.vertices;
        for (int i = 0; i < sectVerts.length; i++) {
            int j = i == 0 ? sectVerts.length - 1 : i - 1;
            if (world.intersect(world.vertices.get(sectVerts[j]), world.vertices.get(sectVerts[i]), location, moveTo)) {
                if (sector.sectors[i] != -1) {
                    System.out.println("move to sector: " + sector.sectors[i]);
                    this.sector = sector.sectors[i];
                } else {
                    moveTo = location;
                }
            }
        }

        this.location = moveTo;
    }

    public void setWorld(World world) {
        this.world = world;
        this.location = world.startLocation;
        this.angle = world.startAngle;
        this.sector = world.startSector;
    }

}
