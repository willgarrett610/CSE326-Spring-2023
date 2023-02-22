package game.renderer;

import game.ResourceLoader;
import game.world.Player;
import game.world.Sector;
import game.world.Vec2f;
import game.world.World;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Renderer {

    Player player;

    int width, height;

    float mapSize = 2f/8f;
    float mapZoom = 5;
    int pointerLength = 20;
    float hFov;
    float vFov;
    int ceilingHeight = 20;
    int eyeHeight = 5;

    Vec2f lookingPoint = null;

    float textureHeight = 20;
    float textureWidth = 20;

    BufferedImage wall;

    public Renderer(Player player, int width, int height) {
        this.player = player;
        this.width = width;
        this.height = height;
        this.hFov = 0.73f*height;
        this.vFov = 0.73f*height;

        wall = ResourceLoader.loadImage("wall.png");
    }

    public void render(BufferStrategy bs) {
        Graphics g = bs.getDrawGraphics();

        World world = player.world;

        float playerAngle = player.angle;
        Vec2f playerLocation = player.location;
        int playerSector = player.sector;

        // Store top and bottom positions of walls all the way across the screen
        // This is used to draw ceiling and floor after walls are finished
        int[] yTop = new int[this.width + 1];
        int[] yBottom = new int[this.width + 1];
        for (int i = 0; i < this.width; i++) yBottom[i] = this.height - 1;

        //  Queue containing the next sectors to be rendered
        //	int sector: 		index of sector to be rendered
        //	int viewingSector: 	index of sector that this sector was seen from
        //	public int leftX:	left side of where sector can be seen on screen
        //	public int rightX;	right side of where sector can be seen on screen
        Queue<QueueItem> renderQueue = new LinkedList<>();
        renderQueue.add(new QueueItem(player.sector, -2, 0, width-1));

        QueueItem head = null;
        while ((head = renderQueue.poll()) != null) {
            Sector sector = world.sectors.get(head.sector);

            renderQueue.addAll(renderSector(g, playerAngle, playerSector, world, sector, head, yTop, yBottom));

            boolean complete = true;
            for (int i = 0; i < width; i++) {
                if (yTop[i] != yBottom[i]) {
                    complete = false;
                    break;
                }
            }
            if (complete)
                break;
        }

        BufferedImage map = renderMap(player.angle);
        g.drawImage(map, 0, 0, null);

        g.dispose();
        bs.show();
    }

    public List<QueueItem> renderSector(
            Graphics g,
            float playerAngle,
            int playerSectorI,
            World world,
            Sector sector,
            QueueItem head,
            int[] yTop,
            int[] yBottom
    ) {
        // The sector the player is standing in currently
        Sector playerSector = world.sectors.get(playerSectorI);

        List<QueueItem> renderQueue = new ArrayList<>();

        // Loop through each wall of the current sector
        for (int i = 0; i < sector.vertices.length; i++) {
            // p1Trans is the vertex to the left of the current vertex index.
            // Since the index to the left of i = 0 would be -1, we wrap around and use the last vertex instead.
            Vec2f p1Trans = translatePoint(world.vertices.get(sector.vertices[(i == 0 ? sector.vertices.length : i) - 1]), playerAngle);
            Vec2f p2Trans = translatePoint(world.vertices.get(sector.vertices[i]), playerAngle);

            // Check if wall is within field of view
            //
            // Not sure if the sector check is required or not
            // Possibly making sure we aren't creating a render loop back and forth between 2 sectors?
            if ((p1Trans.y < 0 && p2Trans.y < 0) || sector.sectors[i] == head.viewingSector) {
                continue;
            }

            // Get screen intersection points with black magic
            Vec2f int1 = getIntersect(p1Trans, p2Trans, new Vec2f(-0.0001f,0.0001f), new Vec2f(-(width/2), 5));
            Vec2f int2 = getIntersect(p1Trans, p2Trans, new Vec2f(0.0001f,0.0001f), new Vec2f((width/2), 5));

            Vec2f p1TransCut = p1Trans;
            Vec2f p2TransCut = p2Trans;

            if (p1Trans.y <= 0) {
                if (int1.y > 0)
                    p1TransCut = int1;
                else
                    p1TransCut = int2;
            }
            if (p2Trans.y <= 0) {
                if (int1.y > 0)
                    p2TransCut = int1;
                else
                    p2TransCut = int2;
            }

            // Scalar values for projecting from 3d to 2d
            Vec2f scale1 = new Vec2f(hFov / p1TransCut.y, vFov / p1TransCut.y);
            Vec2f scale2 = new Vec2f(hFov / p2TransCut.y, vFov / p2TransCut.y);

            // Find x projections
            float x1Proj = p1TransCut.x * scale1.x;
            float x2Proj = p2TransCut.x * scale2.x;
            int heightHalf = this.height / 2;
            int widthHalf = this.width / 2;
            x1Proj += widthHalf;
            x2Proj += widthHalf;

            //Orient left and right x appropriately
            int leftX = (int) x1Proj;
            int rightX = (int) x2Proj;
            Vec2f leftScale = scale1;
            Vec2f rightScale = scale2;

            if (rightX < leftX) {
                leftX = (int) x2Proj;
                rightX = (int) x1Proj;
                leftScale = scale2;
                rightScale = scale1;
            }



            // Calculate ceiling and floor heights with projection
            float heightOff = (sector.floorHeight - playerSector.floorHeight);

            int leftCeilY = (int) (-((sector.ceilingHeight - sector.floorHeight) - eyeHeight + heightOff) * leftScale.y) + heightHalf;
            int leftFloorY = (int) ((eyeHeight - heightOff) * leftScale.y) + heightHalf;
            int rightCeilY = (int) (-((sector.ceilingHeight - sector.floorHeight) - eyeHeight + heightOff) * rightScale.y) + heightHalf;
            int rightFloorY = (int) ((eyeHeight - heightOff) * rightScale.y) + heightHalf;




            int leftPortCeilY = 0;
            int leftPortFloorY = 0;
            int rightPortCeilY = 0;
            int rightPortFloorY = 0;
            Sector portalSector = null;

            // Check if this wall contains a portal to another sector
            if (sector.sectors[i] != -1) {
                // Get sector that portal points to
                portalSector = world.sectors.get(sector.sectors[i]);

                // Same ceiling and floor height calculation as above
                heightOff = (portalSector.floorHeight - playerSector.floorHeight);

                leftPortCeilY = (int) (-((portalSector.ceilingHeight - portalSector.floorHeight) - eyeHeight + heightOff) * leftScale.y) + heightHalf;
                leftPortFloorY = (int) ((eyeHeight - heightOff) * leftScale.y) + heightHalf;
                rightPortCeilY = (int) (-((portalSector.ceilingHeight - portalSector.floorHeight) - eyeHeight + heightOff) * rightScale.y) + heightHalf;
                rightPortFloorY = (int) ((eyeHeight - heightOff) * rightScale.y) + heightHalf;
            }

            // Check if wall is visible within through the portal it's being rendered from
            if (rightX < head.leftX || leftX > head.rightX) {
                continue;
            }

            // Find M and B for y=mx+b form
            double ceilM = (double) (rightCeilY - leftCeilY) / (double) (rightX - leftX);
            double floorM = (double) (rightFloorY - leftFloorY) / (double) (rightX - leftX);
            double ceilB = rightCeilY - (ceilM * (double) rightX);
            double floorB = rightFloorY - (floorM * (double) rightX);

            double portalCeilM = 0;
            double portalFloorM = 0;
            double portalCeilB = 0;
            double portalFloorB = 0;

            if (portalSector != null) {
                // Same y=mx+b stuff for portal
                portalCeilM = (double) (rightPortCeilY - leftPortCeilY) / (double) (rightX - leftX);
                portalFloorM = (double) (rightPortFloorY - leftPortFloorY) / (double) (rightX - leftX);
                portalCeilB = rightPortCeilY - (portalCeilM * (double) rightX);
                portalFloorB = rightPortFloorY - (portalFloorM * (double) rightX);
            }


            // Clamp values to within range of portal so nothing extra is drawn
            rightX = clamp(rightX, head.leftX, head.rightX);
            leftX = clamp(leftX, head.leftX, head.rightX);


            // Get starting y positions for floor and ceil
            double ceilY = ceilM * (double) leftX + ceilB;
            double floorY = floorM * (double) leftX + floorB;
            double portalCeilY = 0;
            double portalFloorY = 0;
            if (portalSector != null) {
                // Add the next sector to the queue if there's a portal
                renderQueue.add(new QueueItem(sector.sectors[i], head.sector, leftX, rightX));

                // Starting y positions
                portalCeilY = portalCeilM * (double) leftX + portalCeilB;
                portalFloorY = portalFloorM * (double) leftX + portalFloorB;
            }

            for (int x = leftX; x <= rightX; x++) {
                // Increment y by its slope
                ceilY += ceilM;
                floorY += floorM;

                // Clamp floor and ceiling to within what hasn't been drawn to
                int ceilYClamp = clamp((int) ceilY, yTop[x], yBottom[x]);
                int floorYClamp = clamp((int) floorY, yTop[x], yBottom[x]);

                // Get 2d point of view ray and wall intersection
                Vec2f worldPoint = screenToWall(x - widthHalf, p1Trans, p2Trans);

                if (x == widthHalf) {
                    this.lookingPoint = worldPoint;
                }

                double wallHeight = sector.ceilingHeight - sector.floorHeight;

                // Calculate distance along wall horizontally
                double dx = worldPoint.distanceTo(p1Trans);

                double screenWallHeight = (int) floorY - (int) ceilY;

                double dyTop = 0;
                double dyBot = 0;

                if (screenWallHeight != 0) {
                    dyTop = (ceilYClamp - (int) ceilY) / screenWallHeight;
                    dyTop *= wallHeight;
                    dyBot = (floorYClamp - (int) ceilY) / screenWallHeight;
                    dyBot *= wallHeight;
                }

                // Calculate texture coordinates normalized from 0 to 1
                double tx = dx / textureWidth;
                double ty = dyTop / textureHeight;
                double th = (dyBot - dyTop) / textureHeight;

                // Draw vertical lines to fill wall

                // Ceiling line
                vLine(g, x, yTop[x], (int) ceilYClamp, Color.DARK_GRAY);
                if (portalSector == null) {
                    // Wall line
                    imgVline(g, x, ceilYClamp, floorYClamp, tx, ty, th, wall);
                }
                // Floor line
                vLine(g, x, floorYClamp, yBottom[x], Color.BLUE);

                yTop[x] = 0;
                yBottom[x] = 0;

                if (portalSector != null) {
                    portalCeilY += portalCeilM;
                    portalFloorY += portalFloorM;
                    int portalCeilYClamp = clamp((int) portalCeilY, (int) ceilYClamp, (int) floorYClamp);
                    int portalFloorYClamp = clamp((int) portalFloorY, (int) ceilYClamp, (int) floorYClamp);

                    // Wall above portal
                    vLine(g, x, ceilYClamp, portalCeilYClamp, Color.WHITE);
                    // Wall below portal
                    vLine(g, x, portalFloorYClamp, floorYClamp, Color.WHITE);

                    // Update available draw area
                    yTop[x] = portalCeilYClamp;
                    yBottom[x] = portalFloorYClamp;
                }

                // Black wireframes
                if (x == leftX || x == rightX)
                    vLine(g, x, ceilYClamp, floorYClamp, Color.BLACK);
            }
        }

        return renderQueue;
    }

    public BufferedImage renderMap(float angle) {
        World world = player.world;

        // Create the map image
        BufferedImage map = new BufferedImage((int) (width * mapSize),(int) (height * mapSize), BufferedImage.TYPE_INT_ARGB);
        Graphics mapG = map.getGraphics();
        Point midP = new Point(map.getWidth() / 2, map.getHeight() / 2);

        // Fill gray background
        mapG.setColor(new Color(52, 52, 52, 100));
        mapG.fillRect(0, 0, map.getWidth(), map.getHeight());

        // Fill current sector in red
        mapG.setColor(Color.RED);
        Sector playerSector = world.sectors.get(player.sector);
        int[] xs = new int[playerSector.vertices.length];
        int[] ys = new int[playerSector.vertices.length];
        for (int i = 0; i < playerSector.vertices.length; i++) {
            Vec2f vert = world.vertices.get(playerSector.vertices[i]);
            vert = translatePoint(vert, angle);
            xs[i] = (int) (vert.x * mapSize * mapZoom) + midP.x;
            ys[i] = (int) (-vert.y * mapSize * mapZoom) + midP.y;
        }
        mapG.fillPolygon(xs, ys, playerSector.vertices.length);

        // Draw walls
        mapG.setColor(Color.YELLOW);
        for (Sector sector : world.sectors) {
            for (int i = 0; i < sector.vertices.length; i++) {
                Vec2f p1Trans = translatePoint(world.vertices.get(sector.vertices[(i == 0 ? sector.vertices.length : i) - 1]), angle);
                Vec2f p2Trans = translatePoint(world.vertices.get(sector.vertices[i]), angle);
                mapG.drawLine((int) (p1Trans.x * mapSize * mapZoom) + midP.x, (int) (-p1Trans.y * mapSize * mapZoom) + midP.y, (int) (p2Trans.x * mapSize * mapZoom) + midP.x, (int) (-p2Trans.y * mapSize * mapZoom) + midP.y);
            }
        }

        // Draw circle for player and line for view direction
        mapG.setColor(Color.GRAY);
        mapG.fillOval(midP.x - 2, midP.y - 2, 4, 4);
        mapG.drawLine(midP.x, midP.y, midP.x, midP.y - pointerLength);

        // Draw green circle on point of wall that is in the center of the screen
        if (lookingPoint != null) {
            mapG.setColor(Color.GREEN);
            mapG.drawOval((int) (lookingPoint.x * mapSize * mapZoom) - 2 + midP.x, (int) (-lookingPoint.y * mapSize * mapZoom) - 2 + midP.y, 4, 4);
        }

        return map;
    }

    public Vec2f translatePoint(Vec2f point, float angle) {
        double rAngle = Math.toRadians(angle);
        return new Vec2f((float) (((point.x - player.location.x) * Math.cos(rAngle)) - ((point.y - player.location.y) * Math.sin(rAngle))),
                (float) (((point.y - player.location.y) * Math.cos(rAngle)) + ((point.x - player.location.x) * Math.sin(rAngle))));
    }

    public Vec2f getIntersect(Vec2f p1, Vec2f p2, Vec2f p3, Vec2f p4) {
        float x = fNCross(p1, p2);
        float y = fNCross(p3, p4);
        float det = fNCross(new Vec2f(p1.x - p2.x, p1.y - p2.y), new Vec2f(p3.x - p4.x, p3.y - p4.y));
        x = fNCross(new Vec2f(x, p1.x - p2.x), new Vec2f(y, p3.x - p4.x)) / det;
        y = fNCross(new Vec2f(x, p1.y - p2.y), new Vec2f(y, p3.y - p4.y)) / det;
        return new Vec2f(x,y);
    }

    public float fNCross(Vec2f p1, Vec2f p2) {
        return p1.x * p2.y - p1.y * p2.x;
    }

    public int clamp(int n, int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return n < min ? min : Math.min(max, n);
    }

    public Vec2f screenToWall(float x, Vec2f p1, Vec2f p2) {
        Vec2f left = p1;
        Vec2f right = p2;
        if (p2.x < left.x) {
            left = p2;
            right = p1;
        }

        // If wall is parallel to view angle
        if (right.x == left.x) {
            float mr = this.hFov / x;
            return new Vec2f(right.x, right.x * mr);
        }

        float mw = (right.y - left.y) / (right.x - left.x);
        float bw = left.y - (mw * left.x);

        // If wall is perpendicular to view angle
        if (x == 0) {
            return new Vec2f(0, bw);
        }

        float mr = this.hFov / x;

        float xOut = -bw / (mw - mr);
        float yOut = mr * xOut;

        return new Vec2f(xOut,yOut);
    }

    public void vLine(Graphics g, int x, int y1, int y2, Color color) {
        g.setColor(color);
        g.fillRect(x, y1, 1, y2 - y1);
        g.setColor(Color.BLACK);
        g.fillRect(x,y1,1,1);
        g.fillRect(x,y2,1,1);
    }

    public Color intToColor(int color) {
        int r = color>>16 & 0xff;
        int g = color>>8 & 0xff;
        int b = color & 0xff;
        return new Color(r,g,b);
    }

    public void imgVline(Graphics g, int x, int y1, int y2, double tx, double ty, double th, BufferedImage image) {
        if (th == 0)
            return;

        // Get the image x position
        int iX = (int)Math.floor(tx * image.getWidth()) % image.getWidth();

        for (int y = y1; y <= y2; y++) {
            // Get the image y position
            float tYNew = (((float) (y - y1) / (float) (y2 - y1)) * (float) th) + (float) ty;
            if (tYNew > 1) tYNew = tYNew - (int) Math.floor(tYNew);

            int iY = (int)Math.floor(tYNew * image.getHeight()) % image.getHeight();

            // Sample pixel from image and draw to screen
            g.setColor(intToColor(wall.getRGB(iX, iY)));
            g.drawLine(x, y, x, y);
        }
    }

}
