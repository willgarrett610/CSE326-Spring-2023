package game;

import game.input.InputListener;
import game.input.MouseListener;
import game.renderer.Renderer;
import game.renderer.Texture;
import game.world.MapLoader;
import game.world.Player;
import game.world.Vec2f;
import game.world.World;
import game.world.entity.Enemy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    static final int FPS = 120;

    static final int width = 800;
    static final int height = 800;

    public Player player;

    private InputListener inputs;

    private MouseListener mouseinputs;

    public Renderer renderer;

    private BufferedImage buf;
    private int screen[];

    BufferedImage loadingImage;

    public JFrame frame;

    private Thread thread;

    boolean running = false;

    public boolean loading = true;
    boolean paused = false;

    boolean inSettings = false;

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

        mouseinputs = new MouseListener();

        this.addKeyListener(inputs);
        this.addMouseMotionListener(inputs);
        this.addMouseListener(mouseinputs);

        renderer = new Renderer(width, height);
        buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = ((DataBufferInt) (buf.getRaster().getDataBuffer())).getData();
    }

    public void loadWorld() {
        // Load map
        World world = null;
        world = MapLoader.load("map.txt");

        player = new Player(world);

        Texture alien = ResourceLoader.loadTexture("alien.jpg");

        Enemy enemy = new Enemy(world, player.location, 1, new Vec2f(10,10), player.sector, alien);

        world.addEntity(enemy);

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
        paused = pauseButton(paused, this.frame);
        inputs.keys_push.clear();
        mouseinputs.mouseclicked = false;
        if (mouseinputs.mouseClick != null) {
            mouseinputs.mouseClick.x = 0;
            mouseinputs.mouseClick.y = 0;
        }
    }

    public synchronized void startLoop() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    @Override
    public void run() {
        long lastUpdate = System.currentTimeMillis();

        JButton resumeButton = new JButton("floor_moon.png");
        Box box = Box.createVerticalBox();
        box.add(resumeButton);
        this.frame.add(box);
        box.grabFocus();

        while (running) {
            // Remain withing frame-rate cap

            long timeElapsed = System.currentTimeMillis() - lastUpdate;
            if ((timeElapsed >= 1000 / FPS) & !paused) {
                lastUpdate = System.currentTimeMillis();
                render(timeElapsed);

            } else if (paused & !inSettings) {
                paused = mouseinputs.pauseButtonCondition_resume(200, 100, 400, 100, frame);
                inSettings = pauseButtonCondition_settings(200, 210, 400, 100);
                mouseinputs.pauseButtonCondition_exit(200, 320, 400, 100);
                mouseinputs.pauseButtonCondition_quit(200, 430, 400, 100, frame);
            } else if (paused & inSettings) {

                inSettings = pauseButtonCondition_settingsExit(200, 500, 400, 100);
            }

            if ((timeElapsed >= 1000 / FPS) & !loading) {
                update();
            }
        }
    }

    public boolean pauseButton(boolean paused, JFrame frame) {
        //var pauseIcon = new ImageIcon("res/wall.png");
        //var pauseLabel = new JLabel(pauseIcon);
        //System.out.println(KeyEvent.KEY_PRESSED);

        if (paused & inputs.keys_push.contains(80)) {
            //frame.add(pauseLabel);

            //Remove cursor
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            frame.getContentPane().setCursor(blankCursor);

            System.out.println("unpaused");
            return false;
        } else if (!paused & inputs.keys_push.contains(80)) {
            //Draws temporary pause buttons
            drawImage(getBufferStrategy(), loadingImage, 50, 50, 625, 625);
            drawImage(getBufferStrategy(), loadingImage, 200, 100, 400, 100);
            drawImage(getBufferStrategy(), loadingImage, 200, 210, 400, 100);
            drawImage(getBufferStrategy(), loadingImage, 200, 320, 400, 100);
            drawImage(getBufferStrategy(), loadingImage, 200, 430, 400, 100);

            //Return cursor
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());

            System.out.println("paused");
            return true;
        }
        return paused;
    }

    //Executes condition for settings button
    public boolean pauseButtonCondition_settings(int x, int y, int w, int h) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                System.out.println("settings");

                drawImage(getBufferStrategy(), loadingImage, 50, 50, 625, 625);
                drawImage(getBufferStrategy(), loadingImage, 200, 500, 400, 100);

                return true;
            }
        }
        return false;
    }

    public boolean pauseButtonCondition_settingsExit(int x, int y, int w, int h) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                System.out.println("settings exit");
                drawImage(getBufferStrategy(), loadingImage, 50, 50, 625, 625);
                drawImage(getBufferStrategy(), loadingImage, 200, 100, 400, 100);
                drawImage(getBufferStrategy(), loadingImage, 200, 210, 400, 100);
                drawImage(getBufferStrategy(), loadingImage, 200, 320, 400, 100);
                drawImage(getBufferStrategy(), loadingImage, 200, 430, 400, 100);
                return false;
            }
        }
        return true;
    }
}
