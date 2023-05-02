package game.menu;

import game.world.Player;

import java.awt.*;

public class HUD {
    private Rectangle healthBar;
    private Rectangle healthContainer;
    private String healthString;
    private Font font;

    /**
     * Constructor for in game user interface
     *
     * @author Isaiah Sandoval
     * @param player Player object
     */
    public HUD(Player player) {
        font = new Font("Roboto", Font.PLAIN, 25);
        int x, y, h;

        x = 50;
        y = 700;
        h = 50;

        healthContainer = new Rectangle(x - 5, y - 5 , 210, h + 10);
//        healthBar = new Rectangle(x, y, health * 2, h);
//        healthString = "Health: " + Integer.toString(health) + "%";
    }

    /**
     * Draws the user interface
     * @param g
     */
    public void draw(Graphics g, Player p) {
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(font);
        healthBar = new Rectangle(50, 700, p.health * 2, 50);

        if (p.health > 0) {
            healthString = "Health: " + Integer.toString(p.health) + "%";
        } else {
            healthString = "DEAD";
        }

        g.setColor(Color.BLACK);
        g2d.fill(healthContainer);
        g.setColor(Color.RED);
        g2d.fill(healthBar);

        int strW, strH;
        g.setColor(Color.WHITE);
        g2d.draw(new Rectangle(healthBar.x, healthBar.y, 200, 50));

        strW = g.getFontMetrics(font).stringWidth(healthString);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(healthString, healthBar.x + 2,
                (int) (healthBar.y + healthBar.height / 2 + strH / 2));

    }
}
