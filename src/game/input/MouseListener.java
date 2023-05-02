package game.input;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class MouseListener extends MouseAdapter {
    public Point mouseClick;
    //public Point mousePos = MouseInfo.getPointerInfo().getLocation();

    public boolean mouseclicked = false;
    public boolean mouseHeld = false;

    //Gets coordinates for when mouse is clicked
    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println(e.getX() + ", " + e.getY());
        mouseClick = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHeld = true;
        mouseclicked = true;
        //System.out.println("Mouse Held");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseHeld = false;
        //System.out.println("Mouse Released");
    }

    //Executes condition for resume button
    public boolean pauseButtonCondition_resume(int x, int y, int w, int h, JFrame frame) {
        if (mouseClick != null) {
            if (mouseClick.getX() > x & mouseClick.getY() > y & mouseClick.getX() < (x + w) & mouseClick.getY() < (y + h)) {
                System.out.println("resume");

                //Remove cursor
                BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                        cursorImg, new Point(0, 0), "blank cursor");
                frame.getContentPane().setCursor(blankCursor);

                return false;
            }
        }

        return true;
    }

}
