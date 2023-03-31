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
        int i = 0;
        int exc = -1;
        int lastSector = this.sector;
        while ((i = wallCollision(moveTo, exc)) != -1) {
            if (i == -2 && this.sector != lastSector) {
                System.out.println("portal recurs");
                exc = getPortalTo(lastSector);
                lastSector = this.sector;
                continue;
            }

            System.out.println("i: " + i + ", sector: " + sector);
            Sector sector = world.sectors.get(this.sector);
            int[] sectVerts = sector.vertices;

            System.out.println("start i = " + i);
            int j = i == 0 ? sectVerts.length - 1 : i - 1;
            //System.out.println(moveTo.x + " " + moveTo.y);
            float buf = (float) 0.1;
            float a = world.vertices.get(sectVerts[j]).x;
            float b = world.vertices.get(sectVerts[j]).y;
            float c = world.vertices.get(sectVerts[i]).x;
            float d = world.vertices.get(sectVerts[i]).y;
            float f = moveTo.x;
            float g = moveTo.y;
            float wall = (b - d) / (a - c); //slope parallel to the wall
            float norm = (a - c) / (b - d); //slope perpendicular to the wall (normal vector)
            if (b - d == 0) { //if the wall is flat on the y-axis
                moveTo.y = b + buf * (location.y - g) / Math.abs(location.y - g);
            } else if (a - c == 0) { //if the wall is flat on the x-axis
                moveTo.x = a + buf * (location.x - f) / Math.abs(location.x - f);
            } else {
                float x_pos = (f * norm + g + a * wall - b) / (wall + norm);
                x_pos += (float) Math.sqrt(buf / (1 + norm * norm)) * (x_pos - f) / Math.abs(x_pos - f); //shift buf units away from the wall
                moveTo = new Vec2f(x_pos, norm * (f - x_pos) + g);
            }
        }

        this.location = moveTo;
    }

    public int getPortalTo(int sectI) {
        Sector sector = world.sectors.get(this.sector);
        for (int i = 0; i < sector.sectors.length; i++) {
            if (sector.sectors[i] == sectI) return i;
        }
        return -1;
    }

    public int wallCollision(Vec2f moveTo, int exclude) {
        Sector sector = world.sectors.get(this.sector);
        int[] sectVerts = sector.vertices;
        boolean portal = false;
        int portI = -1;
        for (int i = 0; i < sectVerts.length; i++) {
            if (i == exclude) continue;
            int j = i == 0 ? sectVerts.length - 1 : i - 1;
            if (world.intersect(world.vertices.get(sectVerts[j]), world.vertices.get(sectVerts[i]), location, moveTo)) {
                if (sector.sectors[i] == -1) {
                    return i;
                } else {
                    portal = true;
                    portI = i;
                }
            }
        }
        if (portal) {
            this.sector = sector.sectors[portI];
            return -2;
        }
        return -1;
    }

    public void setWorld(World world) {
        this.world = world;
        this.location = world.startLocation;
        this.angle = world.startAngle;
        this.sector = world.startSector;
    }

}
