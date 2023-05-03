package game.menu;

import game.Game;
import game.input.MouseListener;
import game.world.Player;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class WinScreen extends MouseListener {
    public boolean active;
    private Font font;
    private Rectangle replayBtn;
    private boolean replayHover = false;
    private String replay = "Replay";
    private Rectangle quitBtn;
    private String quit = "Quit";
    private boolean quitHover = false;
    private String  winString = "YOU WIN!!!";
    private Font winFont;
    Game game;


    public WinScreen(Game game) {
        active = false;
        int w, h;
        w = 300;
        h = 100;

        font = new Font("Monospaced", Font.BOLD, 50);

        replayBtn = new Rectangle(50, 400, w, h);
        quitBtn = new Rectangle(450, 400, w, h);

        this.game = game;
    }

    public void draw (Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(font);
        Color moonBlueFG = new Color(76, 124, 252);
        Color moonBlueBG = new Color(50, 70, 129);
        Color doonRedFG = new Color(213, 185, 43);
        Color doonRedBG = new Color(200, 79, 43);

        g.setColor(Color.RED);
        g2d.fill(replayBtn);
        g2d.fill(quitBtn);

        g.setFont(winFont);
        g.drawString(winString, 200, 300);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g2d.draw(replayBtn);
        g2d.draw(quitBtn);

        int strW, strH;

        //Replay button
        strW = g.getFontMetrics(font).stringWidth(replay);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(replay, (int) (replayBtn.getX() + replayBtn.width / 2 - strW / 2),
                (int) (replayBtn.getY() + replayBtn.height / 2) + strH / 3);

        //Quit button
        strW = g.getFontMetrics(font).stringWidth(quit);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(quit, (int) (quitBtn.x + quitBtn.width / 2 - strW / 2),
                (int) (quitBtn.y + quitBtn.height / 2) + strH / 3);

        //Changes color of replay button if hovered over
        if (replayHover) {
            g.setColor(moonBlueFG);
            g2d.fill(replayBtn);
            g.setColor(moonBlueBG);
            g2d.draw(replayBtn);
            g.setColor(Color.WHITE);
            strW = g.getFontMetrics(font).stringWidth(replay);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(replay, (int) (replayBtn .getX() + replayBtn.width / 2 - strW / 2),
                    (int) (replayBtn.getY() + replayBtn.height / 2) + strH / 3);
        }

        //Changes color of the quit button if hovered over
        if (quitHover) {
            g.setColor(doonRedFG);
            g2d.fill(quitBtn);
            g.setColor(doonRedBG);
            g2d.draw(quitBtn);
            g.setColor(Color.BLACK);
            strW = g.getFontMetrics(font).stringWidth(quit);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(quit, (int) (quitBtn .getX() + quitBtn.width / 2 - strW / 2),
                    (int) (quitBtn.getY() + quitBtn.height / 2) + strH / 3);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (replayBtn.contains(p) && active) {
            // TODO go to main menu?
            game.loadWorlds();
            active = false;

            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            game.frame.getContentPane().setCursor(blankCursor);

            System.out.println("Returning to Menu");
        } else if (quitBtn.contains(p) && active) {
            System.out.println("Exiting game");
            System.exit(0);
        }
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        replayHover = replayBtn.contains(p);
        quitHover = quitBtn.contains(p);
}


}