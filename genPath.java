package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class genPath {
    public enum roadType {
         HV, HH, LV, LH, NONE
    };
    private static TETile[][] tiles;
    static int height;
    static int width;
    static int curX = -1, curY = -1;
    static int nextX = -1,  nextY = -1;
    static roadType nextRoadType = roadType.NONE;
    static roadType curRoadType = roadType.NONE;
    static boolean nextDir = false;
    static int maxRoadLength = 0;

    public  genPath(TETile[][] tiles) {
        this.tiles = tiles;
        maxRoadLength = (int) (0.2 * tiles[0].length * tiles.length);
        height = tiles[0].length;
        width = tiles.length;
    }
    private static roadType getRandomRoadType() {
        Random rand = new Random();
        int tileNum = rand.nextInt(roadType.values().length -2);
        switch (tileNum) {
            case 0: return roadType.HV;
            case 1: return roadType.HH;
            case 2: return roadType.LH;
            case 3: return roadType.LV;
            default: return roadType.NONE;
        }
    }

    public static TETile[][] createRoad(int seedX, int seedY,
                                        boolean isRight,
                                        TETile[][] in_tiles)
    {
        tiles = in_tiles;
        height = in_tiles[0].length;
        width = in_tiles.length;

        Random rand = new Random();

        if (seedX >= width - 2 || seedX < 1 ||
            seedY >= height - 2 || seedY < 1)
        {
            System.out.println("Seed position is out of bounds");
            return tiles;
        }
        boolean direction = isRight;
        //curRoadType = roadType.HH;//getRandomRoadType();
        curRoadType = getRandomRoadType();
        curX  = seedX;
        curY  = seedY;
        int currLength = 0;

        int ntrial = 0;
        while (currLength < maxRoadLength && ntrial++ < 20) {
            System.out.println("len:" + currLength + "<" + maxRoadLength);
            System.out.println("wxh:"+width+'/'+height);
            boolean isGood = false;

            switch (curRoadType) {
                case HV:
                    isGood = setHV(curX, curY, direction);
                    if(!isGood)isGood = setHV(curX, curY, !direction);
                    break;
                case HH:
                    isGood = setHH(curX, curY, direction);
                    if(!isGood)isGood = setHH(curX, curY, !direction);
                    break;
                case LH:
                    isGood = setLH(curX, curY,direction);
                    if(!isGood)isGood = setLH(curX, curY, !direction);
                    break;
                case LV:
                    isGood = setLV(curX, curY,direction);
                    if(!isGood)isGood = setLV(curX, curY, !direction);
                    break;
                default: break;
            }

            curX = nextX;
            curY = nextY;
            curRoadType = nextRoadType;
            currLength++;
            direction = nextDir;

            if(!isGood) continue;

            ntrial = 0;

        }

        return tiles;
    }

    private static boolean setHV(int x, int y, boolean isUP)
    {
        int lefX = x - 1, rightX = x+1;
        if (x < 2 || x >= width-3 || y< 2 || y>= height -3)
            return false;

        nextX = x;
        if( isUP) nextY = y + 1;
        else nextY = y - 1;

        Random rand = new Random();
        int index = rand.nextInt(10);
        if (index == 0) nextRoadType = roadType.LH;
        else nextRoadType = roadType.HV;

        nextDir = rand.nextInt(1)==0?true:false;

        if (isUP) {
            for (int y1 = y; y1 <= y+1; y1++)
            for (int x1 = lefX; x1 <= rightX; x1++){
                if (tiles[x1][y1] != Tileset.NOTHING) return false;
            }

            tiles[x][y] = Tileset.FLOOR;
            tiles[lefX][y] = Tileset.WALL;
            tiles[rightX][y] = Tileset.WALL;

            /*if(tiles[x][y + 2] == Tileset.WALL) {
                tiles[x][y + 1] = Tileset.WALL;
                tiles[lefX][y + 1] = Tileset.WALL;
                tiles[rightX][y + 1] = Tileset.WALL;
            }*/
        } else {
            //down
            for (int y2 = y; y2 >= y-1; y2--) {
                for (int x1 = lefX; x1 <= rightX; x1++){
                    if (tiles[x1][y2] != Tileset.NOTHING) return false;
                }
            }

            tiles[x][y] = Tileset.FLOOR;
            tiles[lefX][y] = Tileset.WALL;
            tiles[rightX][y] = Tileset.WALL;

            /*if(tiles[x][y - 2] == Tileset.WALL) {
                tiles[x][y - 1] = Tileset.WALL;
                tiles[lefX][y - 1] = Tileset.WALL;
                tiles[rightX][y - 1] = Tileset.WALL;
            }*/
        }

        if (y==2 || y == height -3) {
            tiles[x][y] = Tileset.NOTHING;
        }

        return true;
    }


    private static boolean setHH(int x, int y, boolean isRight) {
        int up = y + 1, down = y-1;
        if (x < 2 || x >= width-3 || y< 2 || y>= height -3)
            return false;

        nextY = y;
        if(isRight) nextX = x + 1;
        else nextX = x - 1;

        Random rand = new Random();
        int index = rand.nextInt(10);
        if (index == 0) nextRoadType = roadType.LV;
        else nextRoadType = roadType.HH;

        nextDir = rand.nextInt(1)==0?true:false;

        if (isRight) {
            for (int x1 = x; x1 <= x+1; x1++)
                for (int y1 = down; y1 <= up; y1++){
                    if (tiles[x1][y1] != Tileset.NOTHING) return false;
                }

            tiles[x][y] = Tileset.FLOOR;
            tiles[x][down] = Tileset.WALL;
            tiles[x][up] = Tileset.WALL;

            /*if(tiles[x+2][y] == Tileset.WALL) {
                tiles[x+1][y] = Tileset.WALL;
                tiles[x+1][down] = Tileset.WALL;
                tiles[x+1][up] = Tileset.WALL;
            }*/
        } else {
            //left
            for (int x1 = x; x1 >= x - 1; x1--)
                for (int y1 = down; y1 <= up; y1++) {
                    if (tiles[x1][y1] != Tileset.NOTHING) return false;
                }

            tiles[x][y] = Tileset.FLOOR;
            tiles[x][down] = Tileset.WALL;
            tiles[x][up] = Tileset.WALL;

           /*if (tiles[x - 2][y] == Tileset.WALL) {
                tiles[x - 1][y] = Tileset.WALL;
                tiles[x - 1][down] = Tileset.WALL;
                tiles[x - 1][up] = Tileset.WALL;
            }*/
        }

        if (x==2 || x == width -3) {
            tiles[x][y] = Tileset.NOTHING;
        }

        return true;
    }

    private static boolean setLH(int x, int y, boolean isRight) {
        int lefX = x - 1, rightX = x + 1;
        if (x < 1 || x >= width-2 || y< 1 || y>= height - 2)
            return false;

        for (int y1 = y; y1 <= y + 1; y1++)
            for (int x1 = lefX; x1 <= rightX; x1++)
                if (tiles[x1][y1] != Tileset.NOTHING)
                    return false;

        for (int y1 = y; y1 <= y + 1; y1++)
            for (int x1 = lefX; x1 <= rightX; x1++)
                tiles[x1][y1] = Tileset.WALL;

        tiles[x][y] = Tileset.FLOOR;

        if (isRight) tiles[x + 1][y] = Tileset.FLOOR;
        else tiles[x - 1][y] = Tileset.FLOOR;

        //next road type
        Random rand = new Random();
        int index = rand.nextInt(10);
        if (index == 0) nextRoadType = roadType.LV;
        else nextRoadType = roadType.HH;

        nextDir = rand.nextInt(1)==0?true:false;

        //next point
        if (isRight) nextX = x + 2;
        else nextX = x - 2;
        nextY = y;

        if (y==2 || y == height -3) {
            tiles[x][y] = tiles[x-1][y] = tiles[x+1][y] = Tileset.NOTHING;
        }

        return true;
    }

    private static boolean setLV(int x, int y, boolean isUP) {
        int up = y + 1, down = y - 1;
        if (x < 1 || x >= width-2 || y< 1 || y>= height - 2)
            return false;

        for (int x1 = x; x1 <= x + 1; x1++)
            for (int y1 = down; y1 <= up; y1++)
                if (tiles[x1][y1] != Tileset.NOTHING)
                    return false;

        for (int x1 = x; x1 <= x + 1; x1++)
            for (int y1 = down; y1 <= up; y1++)
                tiles[x1][y1] = Tileset.WALL;

        tiles[x][y] = Tileset.FLOOR;

        if (isUP) tiles[x][y+1] = Tileset.FLOOR;
        else tiles[x][y-1] = Tileset.FLOOR;

        //next road type
        Random rand = new Random();
        int index = rand.nextInt(10);
        if (index == 0) nextRoadType = roadType.LH;
        else nextRoadType = roadType.HV;

        nextDir = rand.nextInt(1)==0?true:false;

        //next point
        if (isUP) nextY = y + 2;
        else nextX = y - 2;
        nextX = x;

        if (x==2 || x == width -3) {
            tiles[x][y] = tiles[x][y-1] = tiles[x][y+1] = Tileset.NOTHING;
        }

        return true;
    }
}
