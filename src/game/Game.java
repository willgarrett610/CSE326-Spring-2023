package game;

import game.input.InputListener;
import game.input.MouseListener;
import game.menu.DeathScreen;
import game.menu.MainMenu;
import game.menu.WinScreen;
import game.renderer.Renderer;
import game.renderer.Texture;
import game.settings.SoundControl;
import game.world.MapLoader;
import game.world.Player;
import game.world.Vec2f;
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
import java.util.List;

import static java.lang.Math.*;

public class Game extends Canvas implements Runnable {

    static final int FPS = 120;

    static final int width = 800;
    static final int height = 800;

    public float volume = 50;

    public Player player;

    private InputListener inputs;

    private MouseListener mouseinputs;

    private SoundControl sound;

    public final MainMenu menu;

    public final WinScreen winScreen;

    public DeathScreen ds;

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
    BufferedImage Settings_Tut;
    BufferedImage Settings_Vol;

    BufferedImage mainMenu;
    BufferedImage gunCursor;

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

    private World level1;
    private World level2;

    public static void main(String[] args) {
        Game game = new Game();
        game.startLoop();
        game.loadResources();
        game.loadWorlds();
        game.loading = false;
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

        sound = new SoundControl();

        mouseinputs = new MouseListener();

        this.addKeyListener(inputs);
        this.addMouseMotionListener(inputs);
        this.addMouseListener(mouseinputs);

        renderer = new Renderer(width, height);
        buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = ((DataBufferInt) (buf.getRaster().getDataBuffer())).getData();
        menu = new MainMenu(this);
        winScreen = new WinScreen(this);

        this.addMouseListener(menu);
        this.addMouseMotionListener(menu);
        this.addMouseListener(winScreen);
        this.addMouseMotionListener(winScreen);
        this.addMouseListener(ds);
        this.addMouseMotionListener(ds);
    }

    public void loadWorlds() {
        // Load map
        level1 = MapLoader.load("level_1.txt");

        player = new Player(level1);

        for (int i = 0; i < level1.alienLocation.size(); i++)
            level1.addEntity(new Alien(level1, level1.alienLocation.get(i), level1.alienSector.get(i), player, alienAnim));

        level2 = MapLoader.load("level_2.txt");

        for (int i = 0; i < level2.alienLocation.size(); i++)
            level2.addEntity(new Alien(level2, level2.alienLocation.get(i), level2.alienSector.get(i), player, alienAnim));

        renderer.setPlayer(player);
    }

    public void loadResources() {
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
        Settings_Tut = ResourceLoader.loadImage("Tutorial.png");
        Settings_Vol = ResourceLoader.loadImage("Volume.png");

        Texture alien15 = ResourceLoader.loadTexture("alien.png");
        Texture alien24 = ResourceLoader.loadTexture("alien_walk_2_4.png");
        Texture alien3 = ResourceLoader.loadTexture("alien_walk_3.png");
        Texture alien68 = ResourceLoader.loadTexture("alien_walk_6_8.png");
        Texture alien7 = ResourceLoader.loadTexture("alien_walk_7.png");
        Texture alien15_dmg1 = ResourceLoader.loadTexture("alien_dmg1.png");
        Texture alien24_dmg1 = ResourceLoader.loadTexture("alien_dmg1_walk_2_4.png");
        Texture alien3_dmg1 = ResourceLoader.loadTexture("alien_dmg1_walk_3.png");
        Texture alien68_dmg1 = ResourceLoader.loadTexture("alien_dmg1_walk_6_8.png");
        Texture alien7_dmg1 = ResourceLoader.loadTexture("alien_dmg1_walk_7.png");
        Texture alien15_dmg2 = ResourceLoader.loadTexture("alien_dmg2.png");
        Texture alien24_dmg2 = ResourceLoader.loadTexture("alien_dmg2_walk_2_4.png");
        Texture alien3_dmg2 = ResourceLoader.loadTexture("alien_dmg2_walk_3.png");
        Texture alien68_dmg2 = ResourceLoader.loadTexture("alien_dmg2_walk_6_8.png");
        Texture alien7_dmg2 = ResourceLoader.loadTexture("alien_dmg2_walk_7.png");

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
        alienAnim.add(alien15_dmg1);
        alienAnim.add(alien24_dmg1);
        alienAnim.add(alien3_dmg1);
        alienAnim.add(alien24_dmg1);
        alienAnim.add(alien15_dmg1);
        alienAnim.add(alien68_dmg1);
        alienAnim.add(alien7_dmg1);
        alienAnim.add(alien68_dmg1);
        alienAnim.add(alien15_dmg2);
        alienAnim.add(alien24_dmg2);
        alienAnim.add(alien3_dmg2);
        alienAnim.add(alien24_dmg2);
        alienAnim.add(alien15_dmg2);
        alienAnim.add(alien68_dmg2);
        alienAnim.add(alien7_dmg2);
        alienAnim.add(alien68_dmg2);

        gunAnim = new ArrayList<>();

        gunAnim.add(gun1);
        gunAnim.add(gun3);
        gunAnim.add(gun2);
        gunAnim.add(gun1);
    }

    public void render(Graphics g, float timeElapsed) {
        if (loading) {
            g.drawImage(loadingImage,0, 0, this.getWidth(), this.getHeight(), null);
            return;
        }

        renderer.render();
        System.arraycopy(renderer.screen, 0, screen, 0, width * height);

        drawMenu(mainMenu, g, this.frame);

        int realFPS = Math.round(1000f / (float) timeElapsed);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("FPS: " + realFPS, 710, 50);
    }

    public void update(Graphics g) {
        //Character cannot move if game is paused
        if (!paused && (player.health > 0)) {
            inputs.checkInput();
        }
        paused = pauseButton(g, paused, this.frame);
        inputs.keys_push.clear();
        mouseinputs.mouseclicked = false;
        if (mouseinputs.mouseClick != null) {
            mouseinputs.mouseClick.x = 0;
            mouseinputs.mouseClick.y = 0;
        }

        // Update entities
        if (!paused && !menu.active) {
            for (Entity e : player.world.entities) {
                e.tick();
            }
        }

        if (player.world.equals(level1)
            && player.location.distanceTo(new Vec2f(50,140)) < 3
            && player.sector == 13) {
            player.setWorld(level2);
        } else if (player.location.distanceTo(new Vec2f(16,70)) < 3 && player.sector == 15) {
            winScreen.active = true;
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());
        }

//        System.out.printf("sector: %d, x: %f, y: %f\n", player.sector, player.location.x, player.location.y);
//        System.out.println(player.location.distanceTo(new Vec2f(120,70)));
    }

    public synchronized void startLoop() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public BufferStrategy startBuffer() {
        BufferStrategy bufStrat = this.getBufferStrategy();
        if (bufStrat == null) {
            this.createBufferStrategy(3);
            return this.getBufferStrategy();
        }
        return bufStrat;
    }

    public void flushBuffer(BufferedImage image, BufferStrategy bufStrat) {
        // Draw image and flush graphics buffer
        do {
            Graphics bufG = bufStrat.getDrawGraphics();
            try {
                bufG.drawImage(image, 0, 0, null);
            } finally {
                bufG.dispose();
            }
            bufStrat.show();
        } while(bufStrat.contentsLost());
    }

    @Override
    public void run() {
        long lastUpdate = System.currentTimeMillis();
        sound.playSound_music(volume);

        while (running) {
            BufferStrategy bufStrat = startBuffer();

            Graphics g = buf.createGraphics();

            // Remain withing frame-rate cap
            mousePos = MouseInfo.getPointerInfo().getLocation();
            mousePos = new Point(mousePos.x - frame.getLocation().x, mousePos.y - frame.getLocation().y);

            long timeElapsed = System.currentTimeMillis() - lastUpdate;
            if (winScreen.active) {
                winScreen.draw(g);
            } else if ((timeElapsed >= 1000 / FPS) & !paused) {
                lastUpdate = System.currentTimeMillis();
                render(g, timeElapsed);

                if (!menu.active || winScreen.active) {
                    if (player.health > 0) {
                        shoot_func(g);
                    }
                }
            } else if (paused & !inSettings) {
                paused = mouseinputs.pauseButtonCondition_resume(200, 100, 400, 100, frame);
                inSettings = pauseButtonCondition_settings(g, 200, 210, 400, 100);
                pauseButtonCondition_reset(g, 200, 320, 400, 100);
                pauseButtonCondition_quit(g, 200, 430, 400, 100, frame);
            } else if (paused & inSettings) {
                inSettings = pauseButtonCondition_settingsExit(g, 200, 500, 400, 100);
                volume = pauseButtonCondition_settingsVolume(g, 200, 180, 400, 30, volume);
            }

            if ((timeElapsed >= 1000 / FPS) && !loading && !winScreen.active) {
                update(g);
            }

            g.dispose();

            flushBuffer(buf, bufStrat);
        }
    }

    public boolean pauseButton(Graphics g, boolean paused, JFrame frame) {
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
            g.drawImage(Pause_Background, 50, 50, 700, 700, null);
            g.drawImage(PauseButton_Resume, 200, 100, 400, 100, null);
            g.drawImage(PauseButton_Settings, 200, 210, 400, 100, null);
            g.drawImage(PauseButton_Reset, 200, 320, 400, 100, null);
            g.drawImage(PauseButton_Quit, 200, 430, 400, 100, null);

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
    public boolean pauseButtonCondition_settings(Graphics g, int x, int y, int w, int h) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                System.out.println("settings");

                //Draw settings menu
                g.drawImage(Pause_Background, 50, 50, 700, 700,null);
                g.drawImage(PauseButton_Exit, 200, 500, 400, 100, null);
                g.drawImage(Settings_Vol, 300, 80, 200, 60, null);
                g.drawImage(Settings_Tut, 130, 200, 500, 250, null);

                int sliderWidth = 400/100 * ((int) volume);

                //Draw sliders
                g.drawImage(Slider_Background, 200, 160, 400, 10, null);
                g.drawImage(Slider_Foreground, 200, 160, sliderWidth, 10, null);

                //reset confirmation conditions
                resetConfirmation = false;
                quitConfirmation = false;

                return true;
            }
        }

        return false;
    }

    public boolean pauseButtonCondition_settingsExit(Graphics g, int x, int y, int w, int h) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                System.out.println("settings exit");

                //Draws pause buttons
                g.drawImage(Pause_Background, 50, 50, 700, 700, null);
                g.drawImage(PauseButton_Resume, 200, 100, 400, 100, null);
                g.drawImage(PauseButton_Settings, 200, 210, 400, 100, null);
                g.drawImage(PauseButton_Reset, 200, 320, 400, 100, null);
                g.drawImage(PauseButton_Quit, 200, 430, 400, 100, null);

                return false;
            }
        }

        return true;
    }

    public float pauseButtonCondition_settingsVolume(Graphics g, int x, int y, int w, int h, float v) {
        float volume = v;

        if (mouseinputs.mouseHeld) {
            if (mousePos.getX() > x & mousePos.getY() > y &
                    mousePos.getX() < (x + w) & mousePos.getY() < (y + h)) {
                volume = (((float) mousePos.getX() - x) / w) * 100;
                //System.out.println("Volume" + volume);
                int sliderWidth = 400/100 * ((int) volume);

                //Draw sliders
                g.drawImage(Slider_Background, 200, 160, 400, 10, null);
                g.drawImage(Slider_Foreground, 200, 160, sliderWidth, 10, null);
            }
        }
        return volume;
    }

    public void shoot_func(Graphics g) {
        if (mouseinputs.mouseclicked == true) {
            if (shoot_cond == false) {
                sound.playSound_shoot(volume);

                //System.out.println("Shoot");
                //System.out.println(player.angle);
                //System.out.println(player.location);

                // Damage alien
                for (int i = 0; i < player.world.entities.size(); i++) {
                    Entity e = player.world.entities.get(i);
                    // TODO: Check if player was aiming at alien
                    if (e instanceof Alien) {
                        Alien alien = (Alien) e;

                        float xDiff = player.location.x - alien.location.x;
                        float yDiff = player.location.y - alien.location.y;
                        double distance = sqrt((xDiff * xDiff) + (yDiff * yDiff));
                        double shootArc = (10 / distance) * 20; //Adjust for wider or smaller angle

                        float shootAng = (float) toDegrees(atan(xDiff / yDiff));

                        float playerShootAng = player.angle % 360;
                        if (player.angle < 0) {
                            playerShootAng = 360 + (player.angle % 360);
                        }

                        if (xDiff < 0 & yDiff < 0) {

                        } else if (xDiff > 0 & yDiff < 0) {
                            shootAng += 360;
                        } else if (xDiff < 0 & yDiff > 0) {
                            shootAng += 180;
                        } else if (xDiff > 0 & yDiff > 0) {
                            shootAng += 180;
                        }

                        //System.out.println(shootAng - playerShootAng);

                        if ((shootAng - playerShootAng) >= -shootArc &
                                (shootAng - playerShootAng) <= shootArc) {
                            System.out.println("Hit!");
                            alien.damage(4);
                        }
                    }
                }
            }
            shoot_cond = true;
        }
        if(this.player != null) {
            shootFrame = player.shoot_anim(gunAnim, shoot_cond);
            if (shootFrame == 0) {
                g.drawImage(gunAnim.get(shootFrame), 400, 350, 450, 450, null);
            } else if (shootFrame == 1) {
                g.drawImage(gunAnim.get(shootFrame), 430, 300, 540, 540, null);
            } else if (shootFrame == 2) {
                g.drawImage(gunAnim.get(shootFrame), 440, 290, 550, 550, null);
            } else if (shootFrame == 3) {
                g.drawImage(gunAnim.get(shootFrame), 410, 340, 470, 470, null);
                shoot_cond = false;
            }
        }
    }

    //Executes condition for exit button
    public void pauseButtonCondition_reset(Graphics g, int x, int y, int w, int h) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                if (resetConfirmation == false) {
                    g.drawImage(PauseButton_Confirm, 200, 320, 400, 100, null);
                    g.drawImage(PauseButton_Confirm, 200, 320, 400, 100, null);
                    resetConfirmation = true;
                } else {
                    this.loadWorlds();
                    paused = false;
                    resetConfirmation = false;
                    System.out.println("Reset level");
                }
            }
        }
    }

    //Executes condition for quit button
    public void pauseButtonCondition_quit(Graphics g, int x, int y, int w, int h, JFrame frame) {
        if (mouseinputs.mouseClick != null) {
            if (mouseinputs.mouseClick.getX() > x & mouseinputs.mouseClick.getY() > y &
                    mouseinputs.mouseClick.getX() < (x + w) & mouseinputs.mouseClick.getY() < (y + h)) {
                if (quitConfirmation == false) {
                    g.drawImage(PauseButton_Confirm, 200, 430, 400, 100, null);
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
    public void drawMenu(BufferedImage mainMenu, Graphics g, JFrame frame) {
        if (menu.active) {
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());
            menu.draw(g, getHeight(), getWidth(), mainMenu);
        } else {
            player.drawInterfaces(g);
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            frame.getContentPane().setCursor(blankCursor);
        }

        if (player.health <= 0) {
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());
            ds = new DeathScreen(player);
            ds.draw(g);
        }
    }
}
