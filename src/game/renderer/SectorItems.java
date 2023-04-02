package game.renderer;

import game.world.Sector;
import game.world.entity.Entity;

import java.util.List;

public class SectorItems {

    protected List<QueueItem> sectors;
    protected List<Entity> entities;

    protected SectorItems(List<QueueItem> sectors, List<Entity> entities) {
        this.sectors = sectors;
        this.entities = entities;
    }

}
