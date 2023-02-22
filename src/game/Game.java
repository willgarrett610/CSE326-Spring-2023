package game;

import game.input.InputListener;
import game.renderer.Renderer;
import game.world.MapLoader;
import game.world.Player;
import game.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class Game {

    static final int FPS = 60;

    static final int width = 800;
    static final int height = 800;

    private JFrame frame;

    private Player player;

    private InputListener inputs;

    public Renderer renderer;

    public static void main(String[] args) {
        Game game = new Game();
        game.startLoop();
    }

    private Game() {
        frame = new JFrame("Moom Doon");
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Center window in monitor
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(2);

        // Remove cursor on window
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(blankCursor);

        // Load map
        World world = null;
        world = MapLoader.load("map.txt");

        player = new Player(world);

        try {
            inputs = new InputListener(player, frame);
        } catch (AWTException e) {
            System.exit(0);
        }
        frame.addKeyListener(inputs);
        frame.addMouseMotionListener(inputs);
    }

    public void startLoop() {
        BufferStrategy bs = frame.getBufferStrategy();
        long lastUpdate = System.currentTimeMillis();
        renderer = new Renderer(player, width, height);
        while(true) {
            // Remain withing frame-rate cap
            if (System.currentTimeMillis() - lastUpdate >= 1000 / FPS) {
                renderer.render(bs);
                lastUpdate = System.currentTimeMillis();
                inputs.checkInput();
            }
        }
    }

}
