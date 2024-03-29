package game.world;

import game.ResourceLoader;
import game.renderer.Texture;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapLoader {

    public static World load(String mapName) {
        List<Vec2f> vertices = new ArrayList<>();
        List<Sector> sectors = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();
        Vec2f playerLocation = null;
        float playerAngle = 0;
        int playerSector = 0;
        List<Vec2f> alienLocation = new ArrayList<>();
        List<Integer> alienSector = new ArrayList<>();

        InputStream mapStream = null;

        mapStream = ClassLoader.getSystemResourceAsStream("res/" + mapName);

        Scanner in = new Scanner(mapStream);

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.startsWith("vertex")) {
                line = line.replaceAll("vertex[ ]+", "");
                String[] nums = line.split("[ ]+");
                for (int i = 1; i < nums.length; i++) {
                    vertices.add(new Vec2f(Float.parseFloat(nums[i]), Float.parseFloat(nums[0])));
                }
            } else if (line.startsWith("texture")) {
                line = line.replaceAll("texture[ ]+", "");
                String[] args = line.split("[ ]+");
                String fileName = args[0];

                Texture img = ResourceLoader.loadTexture(fileName);
                textures.add(img);
            } else if (line.startsWith("sector")) {
                line = line.replaceAll("sector[ ]+", "");
                String[] nums = line.split("[ ]+");
                float floorHeight = Float.parseFloat(nums[0]);
                float ceilHeight = Float.parseFloat(nums[1]);
                int vertexNum = (nums.length - 2) / 3;
                int[] sVertices = new int[vertexNum];
                int[] sSectors = new int[vertexNum];
                int[] sTextures = new int[vertexNum];
                for (int i = 0; i < vertexNum; i++) {
                    sVertices[i] = Integer.parseInt(nums[i + 2]);
                    sSectors[i] = Integer.parseInt(nums[i + 2 + vertexNum]);
                    sTextures[i] = Integer.parseInt(nums[i + 2 + vertexNum * 2]);
                }
                Sector sector = new Sector(floorHeight, ceilHeight, sVertices, sSectors, sTextures);
                sectors.add(sector);
            } else if (line.startsWith("player")) {
                line = line.replaceAll("player[ ]+", "");
                String[] nums = line.split("[ ]+");
                playerLocation = new Vec2f(Float.parseFloat(nums[0]), Float.parseFloat(nums[1]));
                playerAngle = Float.parseFloat(nums[2]);
                playerSector = Integer.parseInt(nums[3]);
            } else if (line.startsWith("alien")) {
                line = line.replaceAll("alien[ ]+", "");
                String[] nums = line.split("[ ]+");
                alienLocation.add(new Vec2f(Float.parseFloat(nums[0]), Float.parseFloat(nums[1])));
                alienSector.add(Integer.parseInt(nums[2]));
            }
        }
        in.close();
        return new World(vertices, sectors, textures, playerLocation, playerAngle, playerSector, alienLocation, alienSector);
    }

}
