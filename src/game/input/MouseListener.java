package game.input;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class MouseListener extends MouseAdapter {
    public Point mouseClick;

    //Gets coordinates for when mouse is clicked
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e.getX() + ", " + e.getY());
        mouseClick = e.getPoint();
    }

    //Executes condition for resume button
    public void pauseButtonCondition_resume(int x, int y, int w, int h) {
        if (mouseClick != null) {
            if (mouseClick.getX() > x & mouseClick.getY() > y & mouseClick.getX() < (x + w) & mouseClick.getY() < (y + h)) {
                System.out.println("resume");
            }
        }
    }

    //Executes condition for settings button
    public void pauseButtonCondition_settings(int x, int y, int w, int h) {
        if (mouseClick != null) {
            if (mouseClick.getX() > x & mouseClick.getY() > y & mouseClick.getX() < (x + w) & mouseClick.getY() < (y + h)) {
                System.out.println("settings");
            }
        }
    }

    //Executes condition for exit button
    public void pauseButtonCondition_exit(int x, int y, int w, int h) {
        if (mouseClick != null) {
            if (mouseClick.getX() > x & mouseClick.getY() > y & mouseClick.getX() < (x + w) & mouseClick.getY() < (y + h)) {
                System.out.println("exit to menu");
            }
        }
    }

    //Executes condition for quit button
    public void pauseButtonCondition_quit(int x, int y, int w, int h) {
        if (mouseClick != null) {
            if (mouseClick.getX() > x & mouseClick.getY() > y & mouseClick.getX() < (x + w) & mouseClick.getY() < (y + h)) {
                System.out.println("quit game");
            }
        }
    }
}
