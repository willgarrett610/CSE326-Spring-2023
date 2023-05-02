package game.world;

import game.renderer.Texture;
import game.world.entity.Entity;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class World {
    public List<Vec2f> vertices;
    public List<Sector> sectors;
    public List<Texture> textures;
    public List<Entity> entities;

    Vec2f startLocation;
    float startAngle;
    int startSector;
    public List<Vec2f> alienLocation;
    public List<Integer> alienSector;

    public World(List<Vec2f> vertices, List<Sector> sectors, List<Texture> textures, Vec2f startLocation, float startAngle, int startSector, List<Vec2f> alienLocation, List<Integer> alienSector) {
        this.vertices = vertices;
        this.sectors = sectors;
        this.textures = textures;
        this.entities = new ArrayList<>();

        this.startLocation = startLocation;
        this.startAngle = startAngle;
        this.startSector = startSector;

        this.alienLocation = alienLocation;
        this.alienSector = alienSector;

        System.out.println("Vertices: \n" + vertices);
        System.out.println("Sectors: \n" + sectors);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public boolean onSegment(Vec2f p, Vec2f q, Vec2f r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y)
                && q.y >= Math.min(p.y, r.y))
            return true;

        return false;
    }

    public int orientation(Vec2f p, Vec2f q, Vec2f r) {
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (val == 0)
            return 0;

        return (val > 0) ? 1 : 2;
    }

    public boolean intersect(Vec2f p1, Vec2f q1, Vec2f p2, Vec2f q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;
        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;
        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;
        if (o4 == 0 && onSegment(p2, q1, q2))
            return true;

        return false;
    }
}
