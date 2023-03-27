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

public class Game extends Canvas implements Runnable {

    static final int FPS = 120;

    static final int width = 800;
    static final int height = 800;

    public Player player;

    private InputListener inputs;

    public Renderer renderer;

    private BufferedImage buf;
    private int screen[];

    BufferedImage loadingImage;

    public JFrame frame;

    private Thread thread;

    boolean running = false;

    public boolean loading = true;
    boolean paused = false;

    public static void main(String[] args) {
        Game game = new Game();
        game.startLoop();
        game.loadWorld();
    }

    private Game() {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        frame = new JFrame("Moom Doon");
        frame.setResizable(false);
        frame.setSize(width, height);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Center window in monitor
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        loadingImage = ResourceLoader.loadImage("loading.png");

        // Remove cursor on window
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(blankCursor);

        try {
            inputs = new InputListener(this);
        } catch (AWTException e) {
            // TODO Give an error message
            System.exit(0);
        }
        this.addKeyListener(inputs);
        this.addMouseMotionListener(inputs);

        renderer = new Renderer(width, height);
        buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = ((DataBufferInt) (buf.getRaster().getDataBuffer())).getData();
    }

    public void loadWorld() {
        // Load map
        World world = null;
        world = MapLoader.load("map.txt");

        player = new Player(world);

        renderer.setPlayer(player);

        loading = false;
    }

    public void drawImage(BufferStrategy bufStrat, BufferedImage image, int x, int y, int w, int h) {
        Graphics g = bufStrat.getDrawGraphics();

        do {
            try {
                g.drawImage(image, x, y, w, h, null);
            } finally {
                g.dispose();
            }
            bufStrat.show();
        } while (bufStrat.contentsLost());
    }

    public void render(float timeElapsed) {
        BufferStrategy bufStrat = getBufferStrategy();
        if (bufStrat == null) {
            this.createBufferStrategy(3);
            return;
        }

        if (loading) {
            drawImage(bufStrat, loadingImage,0, 0, this.getWidth(), this.getHeight());
            return;
        }

        do {
                renderer.render();
                System.arraycopy(renderer.screen, 0, screen, 0, width * height);
                Graphics g = bufStrat.getDrawGraphics();
                try {
                    g.drawImage(buf, 0, 0, null);

                    int realFPS = Math.round(1000f / (float) timeElapsed);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 16));
                    g.drawString("FPS: " + realFPS, 710, 50);
                } finally {
                    g.dispose();
                }
                bufStrat.show();
        } while(bufStrat.contentsLost());
    }

    public void update() {
        //Character cannot move if game is paused
        if (!paused) {
            inputs.checkInput();
        }
        paused = inputs.pauseButton(paused, this.frame);
        inputs.keys_push.clear();
    }

    public synchronized void startLoop() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    @Override
    public void run() {
        long lastUpdate = System.currentTimeMillis();

        while (running) {
            // Remain withing frame-rate cap

            long timeElapsed = System.currentTimeMillis() - lastUpdate;
            if ((timeElapsed >= 1000 / FPS) & !paused) {
                lastUpdate = System.currentTimeMillis();
                render(timeElapsed);

            } else if ((timeElapsed >= 1000 / FPS) & paused) {
                //Draws temporary pause image
                drawImage(getBufferStrategy(), loadingImage,100, 100, 600, 600);
            }

            if ((timeElapsed >= 1000 / FPS) & !loading) {
                update();
            }
        }
    }
}
