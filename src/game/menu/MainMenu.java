package game.menu;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import game.Game;
import game.input.*;

public class MainMenu extends MouseListener {
    public boolean active;

    //buttons
    private Rectangle startBtn;
    private String start = "Start Game";
    private boolean startBtnHover = false;

    private Rectangle exitBtn;
    private String exit = "Exit Game";
    private boolean exitBtnHover = false;

    private Font font;
    private Rectangle startBtnBorder;
    private Rectangle exitBtnBorder;



    /**
     * Constructor for main menu
     * @author Isaiah Sandoval
     */
    public MainMenu (Game game) {
        active = true;

        font = new Font("Monospaced", Font.BOLD, 50);
        //position of buttons
        int x, y;

        //start position
        x = 250;
        y = 500;
        //size of buttons
        int w, h;
        w = 300;
        h = 50;

        startBtn = new Rectangle(x, y, w, h);
        startBtnBorder = new Rectangle(x - 3, y - 3, w + 4, h + 4);

        //exit position
        y = 600;
        exitBtn = new Rectangle(x, y, w, h);
        exitBtnBorder = new Rectangle(x - 3, y - 3, w + 4, h + 4);
    }

    /**
     * Draws the main menu screen
     * @param g Graphics drawer
     * @param frameHeight Height of frame
     * @param frameWidth Width of frame
     * @param mainMenu Main menu image
     * @author Isaiah Sandoval
     */
    public void draw(Graphics g, float frameHeight, float frameWidth, BufferedImage mainMenu) {
        Graphics2D g2d = (Graphics2D) g;
        g.drawImage(mainMenu, 0, 0, (int) frameWidth, (int) frameHeight, null);
        g.setFont(font);
        Color moomBlueFG = new Color(76, 124, 252);
        Color moomBlueBG = new Color(50, 70, 129);
        Color doonRedFG = new Color(213, 185, 43);
        Color doonRedBG = new Color(200, 79, 43);

        //Draws default buttons
        g.setColor(moomBlueFG);
        g2d.fill(startBtn);
        g2d.fill(exitBtn);

        g.setColor(moomBlueBG);
        g2d.setStroke(new BasicStroke(5));
        g2d.draw(startBtnBorder);
        g2d.draw(exitBtnBorder);

        int strW, strH;
        //text color
        g.setColor(Color.WHITE);

        //Start button
        strW = g.getFontMetrics(font).stringWidth(start);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(start, (int) (startBtn.getX() + startBtn.width / 2 - strW / 2),
                (int) (startBtn.getY() + startBtn.height / 2) + strH / 3);


        //Quit button
        strW = g.getFontMetrics(font).stringWidth(exit);
        strH = g.getFontMetrics(font).getHeight();
        g.drawString(exit, (int) exitBtn.getX() + (exitBtn.width / 2) - strW / 2,
                (int) exitBtn.getY() + (exitBtn.height / 2) + strH / 3);

        //Changes color of start button if being hovered over
        if (startBtnHover) {
            g.setColor(doonRedFG);
            g2d.fill(startBtn);
            g.setColor(doonRedBG);
            g2d.draw(startBtnBorder);
            g.setColor(Color.BLACK);
            strW = g.getFontMetrics(font).stringWidth(start);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(start, (int) (startBtn.getX() + startBtn.width / 2 - strW / 2),
                    (int) (startBtn.getY() + startBtn.height / 2) + strH / 3);
        }

        //Changes color of exit button if being hovered over
        if (exitBtnHover) {
            g.setColor(doonRedFG);
            g2d.fill(exitBtn);
            g.setColor(doonRedBG);
            g2d.draw(exitBtnBorder);
            g.setColor(Color.BLACK);
            strW = g.getFontMetrics(font).stringWidth(exit);
            strH = g.getFontMetrics(font).getHeight();
            g.drawString(exit, (int) exitBtn.getX() + (exitBtn.width / 2) - strW / 2,
                    (int) exitBtn.getY() + (exitBtn.height / 2) + strH / 3);
        }

    }

    /**
     * Executes button conditions if buttons are pressed
     * @param e the event to be processed
     * @author Isaiah Sandoval
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (startBtn.contains(p) & active) {
            active = false;
            System.out.println("Starting game");
        } else if (exitBtn.contains(p) & active) {
            System.out.println("Exiting game");
            System.exit(0);
        }
    }

    /**
     * Determines if buttons are being hovered over
     * @param e the event to be processed
     * @author Isaiah Sandoval
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        startBtnHover = startBtn.contains(p);
        exitBtnHover = exitBtn.contains(p);
    }
}
