package game.input;

import game.settings.Settings;
import game.world.Player;
import game.world.Vec2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class InputListener implements KeyListener, MouseMotionListener {

    // List of currently held keys
    List<Integer> keys;
    public List<Integer> keys_push;

    // Current mouse position
    Point mouse;
    boolean ignoreNext;

    Player player;
    JFrame frame;

    // Used to update the mouse position while player is turning
    Robot robot;

    static float movementSpeed = 1f;

    public InputListener(Player player, JFrame frame) throws AWTException {
        this.keys = new ArrayList<>();
        this.keys = new ArrayList<>();

        this.keys_push = new ArrayList<>();

        this.player = player;
        this.frame = frame;
        robot = new Robot();
    }

    public boolean isPressed(char key) {
        return keys.contains(KeyEvent.getExtendedKeyCodeForChar(key));
    }

    public void checkInput() {
        double rAngle = Math.toRadians(player.angle);
        Vec2f moveTo = new Vec2f(player.location.x,player.location.y);
        if (isPressed('A')) {
            moveTo.x -= Math.sin(rAngle + Math.toRadians(90)) * movementSpeed;
            moveTo.y -= Math.cos(rAngle + Math.toRadians(90)) * movementSpeed;
        }
        if (isPressed('D')) {
            moveTo.x += Math.sin(rAngle + Math.toRadians(90)) * movementSpeed;
            moveTo.y += Math.cos(rAngle + Math.toRadians(90)) * movementSpeed;
        }
        if (isPressed('W')) {
            moveTo.x += Math.sin(rAngle) * movementSpeed;
            moveTo.y += Math.cos(rAngle) * movementSpeed;
        }
        if (isPressed('S')) {
            moveTo.x -= Math.sin(rAngle) * movementSpeed;
            moveTo.y -= Math.cos(rAngle) * movementSpeed;
        }

        if (keys.contains(KeyEvent.VK_LEFT)) {
            player.angle -= Settings.mouseSpeed;
        }
        if (keys.contains(KeyEvent.VK_RIGHT)) {
            player.angle += Settings.mouseSpeed;
        }

        player.move(moveTo);
    }

    public boolean pauseButton(boolean paused, JFrame frame) {
        //var pauseIcon = new ImageIcon("res/wall.png");
        //var pauseLabel = new JLabel(pauseIcon);
        //System.out.println(KeyEvent.KEY_PRESSED);
        if (paused & keys_push.contains(80)) {
            //frame.add(pauseLabel);
            System.out.println("unpaused");
            return false;
        } else if (!paused & keys_push.contains(80)) {
            //frame.remove(pauseLabel);
            System.out.println("paused");
            return true;
        }
        return paused;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        isPushed(e);

        if (!keys.contains(e.getKeyCode()))
            keys.add(e.getKeyCode());
    }

    public void isPushed (KeyEvent e) {
        if (keys.contains(e.getKeyCode())){
            keys_push.clear();
            //System.out.println(keys_push);
        } else if (!keys_push.contains(e.getKeyCode()) & !keys.contains(e.getKeyCode())){
            keys_push.add(e.getKeyCode());
            //System.out.println(keys_push);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove((Integer) e.getKeyCode());
        keys_push.clear();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouse == null || ignoreNext) {
            mouse = e.getPoint();
            ignoreNext = false;
            return;
        }

        if (e.getX() < mouse.getX()) {
            player.angle -= (mouse.getX() - e.getX()) * Settings.mouseSpeed;
        } else if(e.getX() > mouse.getX()) {
            player.angle += (e.getX() - mouse.getX()) * Settings.mouseSpeed;
        }

        if (e.getX() < 100) {
            ignoreNext = true;
            Point moveTo = new Point(frame.getLocationOnScreen().x + frame.getWidth() - 200, frame.getLocationOnScreen().y + e.getY());
            robot.mouseMove(moveTo.x, moveTo.y);
            mouse = moveTo;
        } else if (e.getX() > frame.getWidth() - 100) {
            ignoreNext = true;
            Point moveTo = new Point(frame.getLocationOnScreen().x + 200, frame.getLocationOnScreen().y + e.getY());
            robot.mouseMove(moveTo.x, moveTo.y);
            mouse = moveTo;
        } else if (e.getY() < 100) {
            ignoreNext = true;
            Point moveTo = new Point(frame.getLocationOnScreen().x + e.getX(), frame.getLocationOnScreen().y + frame.getHeight() - 200);
            robot.mouseMove(moveTo.x, moveTo.y);
            mouse = moveTo;
        } else if (e.getY() > frame.getHeight() - 100) {
            ignoreNext = true;
            Point moveTo = new Point(frame.getLocationOnScreen().x + e.getX(), frame.getLocationOnScreen().y + 200);
            robot.mouseMove(moveTo.x, moveTo.y);
            mouse = moveTo;
        } else {
            mouse = e.getPoint();
        }
    }
}
