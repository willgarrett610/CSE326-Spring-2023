package game.world.entity;

import game.renderer.Texture;
import game.world.Player;
import game.world.Vec2f;
import game.world.World;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Alien extends Entity {

    final static float SPEED = 0.5f;
    final static int STRENGTH = 15;
    Player player;

    List<Texture> moveAnim;

    private int frameCount = 0;

    public static final int maxHealth = 50;
    private int health;

    private long lastAttack;
    private final static long attackDelay = 500;

    public Alien(World world, Vec2f location, int sector, Player player, List<Texture> moveAnim) {
        super(world, location, 0, new Vec2f((float) 5.2, (float) 9.8), sector);
        this.player = player;
        this.moveAnim = moveAnim;
        this.health = maxHealth;
    }

    @Override
    public void tick() {
        tryAttack();
        moveTowardPlayer(player);
    }

    @Override
    public Texture getTexture() {
        Texture texture;
        if ((float)health / (float)maxHealth >= 0.666)
            texture = moveAnim.get(Math.floorDiv(frameCount, 5) % (moveAnim.size() / 3)).clone();
        else if ((float)health / (float)maxHealth >= 0.333)
            texture = moveAnim.get(8 + Math.floorDiv(frameCount, 5) % (moveAnim.size() / 3)).clone();
        else
            texture = moveAnim.get(16 + Math.floorDiv(frameCount, 5) % (moveAnim.size() / 3)).clone();

        int healthBarLevel = (int) Math.floor(((float)health/(float)maxHealth) * texture.getWidth());

        for (int x = 0; x < texture.getWidth(); x++) {
            for (int y = 0; y < 5; y++) {
                int color = x < healthBarLevel ? 0xffff0000 : 0xff000000;
                texture.setPixel(x,y,color);
            }
        }
        return texture;
    }

    public void moveTowardPlayer(Player player) {
        if (this.location.distanceTo(player.location) <= 10) return;

        int animLength = moveAnim.size();
        frameCount++;
        // About to overflow and at new frame loop
        if (frameCount >= Integer.MAX_VALUE - animLength - 1 && frameCount % animLength == 0) {
            frameCount = 0;
        }

        Vec2f vel = this.getLocation().pointToNormalized(player.location);
        vel = vel.multiply(SPEED, SPEED);
        this.move(this.getLocation().add(vel));
    }

    public void kill() {
        this.world.removeEntity(this);
    }

    public void damage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.kill();
        }
    }

    public void tryAttack() {
        if (System.currentTimeMillis() - this.lastAttack >= attackDelay
                && this.location.distanceTo(player.location) <= 13
                && this.sector == player.sector) {
            lastAttack = System.currentTimeMillis();
            player.health -= 5;
        }
    }

}
