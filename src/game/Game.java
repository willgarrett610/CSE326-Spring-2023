package game;

import game.input.InputListener;
import game.input.MouseListener;
import game.menu.MainMenu;
import game.menu.UI;
import game.renderer.Renderer;
import game.renderer.Texture;
import game.world.MapLoader;
import game.world.Player;
import game.world.World;
import game.world.entity.Alien;
import game.world.entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends Canvas implements Runnable {

    static final int FPS = 120;

    static final int width = 800;
    static final int height = 800;

    public float volume = 50;

    public Player player;

    private InputListener inputs;

    private MouseListener mouseinputs;

    public final MainMenu menu;
    private UI ui;

    public Renderer renderer;

    private BufferedImage buf;
    private int screen[];

    Point mousePos;

    BufferedImage loadingImage;
    BufferedImage PauseButton_Resume;
    BufferedImage PauseButton_Settings;
    BufferedImage PauseButton_Reset;
    BufferedImage PauseButton_Quit;
    BufferedImage PauseButton_Exit;
    BufferedImage PauseButton_Confirm;
    BufferedImage Pause_Background;
    BufferedImage Slider_Background;
    BufferedImage Slider_Foreground;

    BufferedImage mainMenu;

    List<Texture> alienAnim;

    List<BufferedImage> gunAnim;

    BufferedImage gun1;
    BufferedImage gun2;
    BufferedImage gun3;
    boolean shoot_cond = false;
    int shootFrame = 0;

    public JFrame frame;

    private Thread thread;

    boolean running = false;

    public boolean loading = true;
    public boolean paused = false;

    public boolean inSettings = false;
    public boolean resetConfirmation = false;
    public boolean quitConfirmation = false;

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
        PauseButton_Resume = ResourceLoader.loadImage("ResumeButton.png");
        PauseButton_Settings = ResourceLoader.loadImage("SettingsButton.png");
        PauseButton_Reset = ResourceLoader.loadImage("ResetButton.png");
        PauseButton_Quit = ResourceLoader.loadImage("QuitButton.png");
        PauseButton_Exit = ResourceLoader.loadImage("ExitButton.png");
        PauseButton_Confirm = ResourceLoader.loadImage("ConfirmationButton.png");
        Pause_Background = ResourceLoader.loadImage("PauseBackground.png");
        Slider_Background = ResourceLoader.loadImage("SliderBackground.png");
        Slider_Foreground = ResourceLoader.loadImage("SliderForeground.png");
        mainMenu = ResourceLoader.loadImage("mainmenu.png");

        Texture alien15 = ResourceLoader.loadTexture("alien.png");
        Texture alien24 = ResourceLoader.loadTexture("alien_walk_2_4.png");
        Texture alien3 = ResourceLoader.loadTexture("alien_walk_3.png");
        Texture alien68 = ResourceLoader.loadTexture("alien_walk_6_8.png");
        Texture alien7 = ResourceLoader.loadTexture("alien_walk_7.png");

        gun1 = ResourceLoader.loadImage("gunFrame1.png");
        gun2 = ResourceLoader.loadImage("gunFrame2.png");
        gun3 = ResourceLoader.loadImage("gunFrame3.png");

        alienAnim = new ArrayList<>();

        alienAnim.add(alien15);
        alienAnim.add(alien24);
        alienAnim.add(alien3);
        alienAnim.add(alien24);
        alienAnim.add(alien15);
        alienAnim.add(alien68);
        alienAnim.add(alien7);
        alienAnim.add(alien68);

        gunAnim = new ArrayList<>();

        gunAnim.add(gun1);
        gunAnim.add(gun3);
        gunAnim.add(gun2);
        gunAnim.add(gun1);

        // Remove cursor on window
//        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//                cursorImg, new Point(0, 0), "blank cursor");
//        frame.getContentPane().setCursor(blankCursor);

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
        menu = new MainMenu(this);

        this.addMouseListener(menu);
        this.addMouseMotionListener(menu);
    }

    public void loadWorld() {
        // Load map
        World world = null;
        world = MapLoader.load("level_1.txt");

        player = new Player(world);

        Alien enemy = new Alien(world, player.location, player.sector, player, alienAnim);

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
                    drawMenu(mainMenu, g);

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

        // Update entities
        for (Entity e : player.world.entities) {
            e.tick();
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


        while (running) {
            // Remain withing frame-rate cap
            mousePos = MouseInfo.getPointerInfo().getLocation();
            mousePos = new Point(mousePos.x - frame.getLocation().x, mousePos.y - frame.getLocation().y);

            long timeElapsed = System.currentTimeMillis() - lastUpdate;
            if ((timeElapsed >= 1000 / FPS) & !paused) {
                lastUpdate = System.currentTimeMillis();
                render(timeElapsed);

                if (!menu.active) {
                    shoot_func();
                }
            } else if (paused & !inSettings) {
                paused = mouseinputs.pauseButtonCondition_resume(200, 100, 400, 100, frame);
                inSettings = pauseButtonCondition_settings(200, 210, 400, 100);
                pauseButtonCondition_reset(200, 320, 400, 100);
                pauseButtonCondition_quit(200, 430, 400, 100, frame);
            } else if (paused & inSettings) {
                inSettings = pauseButtonCondition_settingsExit(200, 500, 400, 100);
                volume = pauseButtonCondition_settingsVolume(200, 320, 400, 30, volume);
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
            //Draws pause buttons
            drawImage(getBufferStrategy(), Pause_Background, 50, 50, 700, 700);
            drawImage(getBufferStrategy(), PauseButton_Resume, 200, 100, 400, 100);
            drawImage(getBufferStrategy(), PauseButton_Settings, 200, 210, 400, 100);
            drawImage(getBufferStrategy(), PauseButton_Reset, 200, 320, 400, 100);
            drawImage(getBufferStrategy(), PauseButton_Quit, 200, 430, 400, 100);
            drawImage(getBufferStrategy(), PauseButton_Quit, 200, 430, 400, 100);

            //Return cursor
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());

            resetConfirmation = false;
            quitConfirmation = false;

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

                //Draw settings menu
                drawImage(getBufferStrategy(), Pause_Background, 50, 50, 700, 700);
                drawImage(getBufferStrategy(), PauseButton_Exit, 200, 500, 400, 100);

                int sliderWidth = 400/100 * ((int) volume);

                //Draw sliders
                drawImage(getBufferStrategy(), Slider_Background, 200, 300, 400, 10);
                drawImage(getBufferStrategy(), Slider_Foreground, 200, 300, sliderWidth, 10);
                drawImage(getBufferStrategy(), Slider_Foreground, 200, 300, sliderWidth, 10);

                //reset confirmation conditions
                resetConfirmation = false;
                quitConfirmation = false;

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

                //Draws pause buttons
                drawImage(getBufferStrategy(), Pause_Background, 50, 50, 700, 700);
                drawImage(getBufferStrategy(), PauseButton_Resume, 200, 100, 400, 100);
                drawImage(getBufferStrategy(), PauseButton_Settings, 200, 210, 400, 100);
                drawImage(getBufferStrategy(), PauseButton_Reset, 200, 320, 400, 100);
                drawImage(getBufferStrategy(), PauseButton_Quit, 200, 430, 400, 100);
                drawImage(getBufferStrategy(), PauseButton_Quit, 200, 430, 400, 100);

                return false;
            }
        }
        return true;
    }

    public float pauseButtonCondition_settingsVolume(int x, int y, int w, int h, float v) {
        float volume = v;

        if (mouseinputs.mouseHeld) {
            if (mousePos.getX() > x & mousePos.getY() > y &
                    mousePos.getX() < (x + w) & mousePos.getY() < (y + h)) {
                volume = (((float) mousePos.getX() - x) / w) * 100;
                //System.out.println("Volume" + volume);
                int sliderWidth = 400/100 * ((int) volume);

                //Draw sliders
                drawImage(getBufferStrategy(), Slider_Background, 200, 300, 400, 10);
                drawImage(getBufferStrategy(), Slider_Foreground, 200, 300, sliderWidth, 10);
                drawImage(getBufferStrategy(), Slider_Foreground, 200, 300, sliderWidth, 10);
            }
        }
        return volume;
    }

    public void shoot_func() {
        if (mouseinputs.mouseclicked == true) {
            shoot_cond = true;
        }
        if(this.player != null) {
            shootFrame = player.shoot_anim(gunAnim, shoot_cond);
            if (shootFrame == 0) {
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 400, 350, 450, 450);
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 400, 350, 450, 450);
            } else if (shootFrame == 1) {
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 430, 300, 540, 540);
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 430, 300, 540, 540);
            } else if (shootFrame == 2) {
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 440, 290, 550, 550);
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 440, 290, 550, 550);
            } else if (shootFrame == 3) {
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 410, 340, 470, 470);
                drawImage(getBufferStrategy(), gunAnim.get(shootFrame), 410, 340, 470, 470);
                shoot_cond = false;
            }
        }
    }

    //Executes condition for exit button
    public void pauseButtonCondition_reset(int x, int y, int w, int h) {

        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                if (resetConfirmation == false) {
                    drawImage(getBufferStrategy(), PauseButton_Confirm, 200, 320, 400, 100);
                    drawImage(getBufferStrategy(), PauseButton_Confirm, 200, 320, 400, 100);
                    resetConfirmation = true;
                } else {
                    this.loadWorld();
                    paused = false;
                    resetConfirmation = false;
                    System.out.println("Reset level");
                }
            }
        }
    }

    //Executes condition for quit button
    public void pauseButtonCondition_quit(int x, int y, int w, int h, JFrame frame) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                if (quitConfirmation == false) {
                    drawImage(getBufferStrategy(), PauseButton_Confirm, 200, 430, 400, 100);
                    drawImage(getBufferStrategy(), PauseButton_Confirm, 200, 430, 400, 100);
                    quitConfirmation = true;
                } else {
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    System.out.println("quit game");
                }
            }
        }
    }

    /**
     * Draws the main menu
     *
     * @param mainMenu preloaded main menu image
     * @param g graphics drawer
     * @author Isaiah Sandoval
     */
    public void drawMenu(BufferedImage mainMenu, Graphics g) {
        if (menu.active) {
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());
            menu.draw(g, getHeight(), getWidth(), mainMenu);
        } else {
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
            frame.getContentPane().setCursor(blankCursor);
        }
    }
}
