package game.renderer;

import game.world.Sector;
import game.world.entity.Entity;

import java.util.List;

public class SectorItems {

    protected List<QueueItem> sectors;
    protected List<Sprite> sprites;

    protected SectorItems(List<QueueItem> sectors, List<Sprite> sprites) {
        this.sectors = sectors;
        this.sprites = sprites;
    }

}
