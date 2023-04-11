package game.renderer;

public class Sprite {

    public int x;
    public int y;
    public int width;
    public int height;
    int[][] columns;

    public Sprite(int x, int y, int width, int height, int[][] columns) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.columns = columns;
    }

    public void renderTo(int[] pixels, int width, int height) {
        for (int iX = 0; iX < this.width; iX++) {
            for (int iY = 0; iY < this.height; iY++) {
                int sX = iX + this.x;
                int sY = iY + this.y;

                int pixel = columns[iX][iY];

                if (pixel == 0) continue;

                pixels[sX + sY * width] = pixel;
            }
        }
    }

}
