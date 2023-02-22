package game.world;

import java.util.Arrays;

public class Sector {

    public float floorHeight;
    public float ceilingHeight;
    public int[] vertices;
    public int[] sectors;

    public Sector(float floorHeight, float ceilingHeight, int[] vertices, int[] sectors) {
        this.floorHeight = floorHeight;
        this.ceilingHeight = ceilingHeight;
        this.vertices = vertices;
        this.sectors = sectors;
    }

    @Override
    public String toString() {
        return this.floorHeight + " " + this.ceilingHeight + " ; " + Arrays.toString(vertices) + " ; "
                + Arrays.toString(sectors);
    }
}
