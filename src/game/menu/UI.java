package game.menu;

import java.awt.*;
import game.world.Player;

public class UI {

    public boolean active;
    private Rectangle healthBar;
    private Rectangle healthContainer;
    private String healthString;
    private Font font;
    Player player;


    public UI() {
        active = false;
        font = new Font("Roboto", Font.PLAIN, 10);
        int x, y, h;

        x = 50;
        y = 50;
        h = 50;

        healthContainer = new Rectangle(x - 5, y - 5 , 210, h + 10);
        healthBar = new Rectangle(x, y, player.health * 2, h);
        healthString = "Health: " + Integer.toString(player.health) + "%";
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(font);

        g.setColor(Color.BLACK);
        g2d.fill(healthContainer);
        g.setColor(Color.RED);
        g2d.fill(healthBar);

        int strW, strH;
        g.setColor(Color.WHITE);

        strW = g.getFontMetrics(font).stringWidth(healthString);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(healthString, (int) (healthBar.x + healthBar.width / 2 - strW / 2),
                (int) (healthBar.y + healthBar.height / 2 + strH / 2));

    }
}
