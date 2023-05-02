package game.world;

import game.Game;
import game.menu.DeathScreen;
import game.menu.HUD;
import game.renderer.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player extends Moveable {
    public float angle;
    public int health;

    //Gun frame information
    List<Texture> shootAnim;
    private int frameCount = 0;

    public Player(World world) {
        super(world, world.startLocation, world.startSector);
        this.angle = world.startAngle;
        this.health = 100;
    }

    public void setWorld(World world) {
        this.world = world;
        this.location = world.startLocation;
        this.sector = world.startSector;
        this.angle = world.startAngle;
    }

    public void shoot() {
        System.out.println("Bang!");
    }

    public int shoot_anim(List<BufferedImage> shootAnim, boolean shooting) {
        int animLength = shootAnim.size();
        if (shooting) {
            frameCount++;
            // About to overflow and at new frame loop
            if ((frameCount >= Integer.MAX_VALUE - animLength - 1 && frameCount % animLength == 0) ||
                    frameCount > ((animLength - 1) * 3)) {
                frameCount = 0;
            }
            //System.out.println(Math.floorDiv(frameCount, 3) % shootAnim.size());
            //System.out.println(frameCount);
            return Math.floorDiv(frameCount, 3) % animLength;
        }
        return 0;
    }

    public void drawInterfaces(Graphics g) {
        HUD hud = new HUD(this);
        hud.draw(g, this);
    }

    //    public void drawHUD(Graphics g) {
//        HUD hud = new HUD(this);
//        hud.draw(g, this);
//    }
//
//    public void drawDeathScreen(Graphics g) {
//        DeathScreen ds = new DeathScreen(this);
//        ds.draw(g);
//    }
}
