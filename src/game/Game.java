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
import java.awt.image.DataBufferInt;
import java.io.FileNotFoundException;

public class Game {

    static final int FPS = 120;

    static final int width = 800;
    static final int height = 800;

    private JFrame frame;

    private Player player;

    private InputListener inputs;

    public Renderer renderer;

    private BufferedImage buf;
    private int screen[];

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
        BufferStrategy bufStrat = frame.getBufferStrategy();
        buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = ((DataBufferInt) (buf.getRaster().getDataBuffer())).getData();
        long lastUpdate = System.currentTimeMillis();
        renderer = new Renderer(player, width, height);
        boolean paused = false;
        while(true) {
            // Remain withing frame-rate cap
            paused = inputs.pauseButton(paused, frame);
            long timeElapsed = System.currentTimeMillis() - lastUpdate;
            if ((timeElapsed >= 1000 / FPS) & !paused) {
                lastUpdate = System.currentTimeMillis();
                renderer.render();
                System.arraycopy(renderer.screen, 0, screen, 0, width * height);

                Graphics g = bufStrat.getDrawGraphics();
                g.drawImage(buf, 0, 0, null);

                int realFPS = Math.round(1000f / (float)timeElapsed);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("FPS: " + realFPS, 710, 50);

                bufStrat.show();
                g.dispose();
                inputs.checkInput();
            }

        }
    }
}
