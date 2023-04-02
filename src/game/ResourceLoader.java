package game;

import game.renderer.Texture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

public class ResourceLoader {

    public static URL getPath(String name) {
        System.out.println("loading " + name);
        return ClassLoader.getSystemResource("res/" + name);
    }

    public static BufferedImage loadImage(String name) {
        Image img = new ImageIcon(getPath(name)).getImage();
        BufferedImage bImg = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        bImg.getGraphics().drawImage(img, 0, 0, null);

        return bImg;
    }

    public static Texture loadTexture(String name) {
        BufferedImage img = loadImage(name);
        return new Texture(img);
    }

}
