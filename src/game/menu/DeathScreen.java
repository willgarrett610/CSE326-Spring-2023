package game.menu;

import game.Game;
import game.input.MouseListener;
import game.world.Player;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeathScreen extends MouseAdapter {
    public boolean active;
    private Font font;
    private Rectangle retryBtn;
    private boolean retryHover = false;
    private String retry = "Retry";
    private Rectangle quitBtn;
    private String quit = "Quit";
    private boolean quitHover = false;
    private String deathString = "YOU DIED";
    private Font deathFont;
    Game game;

    public DeathScreen(Game game) {
        active = false;
        int w, h;
        w = 300;
        h = 100;

        font = new Font("Monospaced", Font.BOLD, 50);
        deathFont = new Font("Monospaced", Font.BOLD, 100);

        retryBtn = new Rectangle(50, 450, w, h);
        quitBtn = new Rectangle(450, 450, w, h);

        this.game = game;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(deathFont);
        Color moomBlueFG = new Color(76, 124, 252);
        Color moomBlueBG = new Color(50, 70, 129);
        Color doonRedFG = new Color(213, 185, 43);
        Color doonRedBG = new Color(200, 79, 43);

        g.setColor(Color.RED);
        g2d.fill(retryBtn);
        g2d.fill(quitBtn);
        g.drawString(deathString, 200, 300);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g2d.draw(retryBtn);
        g2d.draw(quitBtn);


        int strW, strH;
        g.setFont(font);

        //Retry button
        strW = g.getFontMetrics(font).stringWidth(retry);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(retry, (int) (retryBtn.getX() + retryBtn.width / 2 - strW / 2),
                (int) (retryBtn.getY() + retryBtn.height / 2) + strH / 3);

        //Quit button
        strW = g.getFontMetrics(font).stringWidth(quit);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(quit, (int) (quitBtn.x + quitBtn.width / 2 - strW / 2),
                (int) (quitBtn.y + quitBtn.height / 2) + strH / 3);

        //Changes color of start button if being hovered over
        if (retryHover) {
            g.setColor(moomBlueFG);
            g2d.fill(retryBtn);
            g.setColor(moomBlueBG);
            g2d.draw(retryBtn);
            g.setColor(Color.WHITE);
            strW = g.getFontMetrics(font).stringWidth(retry);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(retry, (int) (retryBtn.getX() + retryBtn.width / 2 - strW / 2),
                    (int) (retryBtn.getY() + retryBtn.height / 2) + strH / 3);
        }


        if (quitHover) {
            g.setColor(doonRedFG);
            g2d.fill(quitBtn);
            g.setColor(doonRedBG);
            g2d.draw(quitBtn);
            g.setColor(Color.BLACK);
            strW = g.getFontMetrics(font).stringWidth(quit);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(quit, (int) (quitBtn.getX() + quitBtn.width / 2 - strW / 2),
                    (int) (quitBtn.getY() + quitBtn.height / 2) + strH / 3);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (retryBtn.contains(p) && active) {
            game.loadWorlds();
            active = false;
            System.out.println("Retrying");
        } else if (quitBtn.contains(p) && active) {
            System.out.println("Exiting game");
            System.exit(0);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        retryHover = retryBtn.contains(p);
        quitHover = quitBtn.contains(p);
    }
}
