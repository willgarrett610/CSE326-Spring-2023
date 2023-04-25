package game.world.entity;

import game.renderer.Texture;
import game.world.Player;
import game.world.Vec2f;
import game.world.World;

import java.util.List;

public class Alien extends Entity {

    final static float SPEED = 0.5f;
    final static int STRENGTH = 15;
    Player player;

    List<Texture> moveAnim;

    private int frameCount = 0;

    public Alien(World world, Vec2f location, int sector, Player player, List<Texture> moveAnim) {
        super(world, location, 0, new Vec2f((float) 5.2, (float) 9.8), sector);
        this.player = player;
        this.moveAnim = moveAnim;
    }

    @Override
    public void tick() {
        int animLength = moveAnim.size();
        frameCount++;
        // About to overflow and at new frame loop
        if (frameCount >= Integer.MAX_VALUE - animLength - 1 && frameCount % animLength == 0) {
            frameCount = 0;
        }

        moveTowardPlayer(player);
    }

    @Override
    public Texture getTexture() {
        return moveAnim.get(Math.floorDiv(frameCount, 5)%moveAnim.size());
    }

    public void moveTowardPlayer(Player player) {
//        System.out.println("Alien: " + this.location);
//        System.out.println("Player: " + player.location);
        Vec2f vel = this.getLocation().pointToNormalized(player.location);
        vel = vel.multiply(SPEED, SPEED);
        this.move(this.getLocation().add(vel));
    }

}
