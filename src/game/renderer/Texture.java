package game.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Texture {

    private int width;
    private int height;
    private int[][] columns;

    public Texture(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();

        int[] data = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
        columns = new int[image.getWidth()][];

        for (int x = 0; x < image.getWidth(); x++) {
            int[] column = new int[image.getHeight()];
            for (int y = 0; y < image.getHeight(); y++) {
                int i = x + y * image.getWidth();
                column[y] = data[i];
            }
            columns[x] = column;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getColumn(int x) {
        return columns[x];
    }

    public int getPixel(int x, int y) {
        return columns[x][y];
    }

}
