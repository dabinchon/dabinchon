package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Long.parseLong;

public class genRoomPath {
    public ArrayList<Integer> wallList;

    private ArrayList<Integer> roomListX;
    private ArrayList<Integer> roomListY;
    private ArrayList<Integer> doorWorld;
    private static TETile[][] tiles;
    static int height;
    static int width;
    int nRoom = 0;
     String command = "";
    int Xmover = -1;
    int Ymover = -1;

    ArrayList<Integer> lightList;
    ArrayList<TETile> floorTypeList;
    ArrayList<TETile> lightTypeList;
    ArrayList<Integer> lightOffList;
    ArrayList<Integer> teleportList;
    ArrayList<Integer> mountainList;

    public genRoomPath(TETile[][] tiles) {
        wallList = new ArrayList<>();
        doorWorld = new ArrayList<>();

        roomListX = new ArrayList<>();
        roomListY = new ArrayList<>();

        teleportList = new ArrayList<>();
        mountainList = new ArrayList<>();



        this.tiles = tiles;
        height = tiles[0].length;
        width = tiles.length;

        lightList = new ArrayList<>();
        lightOffList = new ArrayList<Integer>();

        lightTypeList = new ArrayList<>();
        lightTypeList.add(Tileset.LIGHT0_B);
        lightTypeList.add(Tileset.LIGHT1_B);
        lightTypeList.add(Tileset.LIGHT2_B);
        lightTypeList.add(Tileset.LIGHT3_B);
        lightTypeList.add(Tileset.LIGHT0_G);
        lightTypeList.add(Tileset.LIGHT1_G);
        lightTypeList.add(Tileset.LIGHT2_G);
        lightTypeList.add(Tileset.LIGHT3_G);
        lightTypeList.add(Tileset.LIGHT0_R);
        lightTypeList.add(Tileset.LIGHT1_R);
        lightTypeList.add(Tileset.LIGHT2_R);
        lightTypeList.add(Tileset.LIGHT3_R);

        floorTypeList = new ArrayList<>();
        floorTypeList.add(Tileset.FLOOR);
        floorTypeList.add(Tileset.TELEPORT);
        floorTypeList.add(Tileset.MOUNTAIN);
        floorTypeList.addAll(lightTypeList);
    }

    int getXmover() {
        return Xmover;
    }

    int getYmover() {
        return Ymover;
    }

    Boolean runCommandSwitch(char c)
    {
        char c_switch = c;
        if (c == 'A') c_switch = 'D';
        else if (c == 'D')  c_switch = 'A';
        else if (c == 'W')  c_switch = 'S';
        else if (c == 'S')  c_switch = 'W';

        return runCommand(c_switch);
    }

    Boolean runCommand(char c) {
        if (Xmover == -1 || Ymover == -1) {
            Xmover = roomListX.get(0);
            Ymover = roomListY.get(0);
            tiles[Xmover][Ymover] = Tileset.AVATAR;
        }

        command = command + String.valueOf(c);

        if (c == 'W' || c == 'w') {
            if (floorTypeList.contains(tiles[Xmover][Ymover + 1])) {
                tiles[Xmover][Ymover++] = Tileset.FLOOR;

                if(tiles[Xmover][Ymover] == Tileset.TELEPORT){
                   int po = getTelePort(Xmover+ Ymover * width);
                    Xmover = po % width;
                    Ymover = po / width;
                }

                tiles[Xmover][Ymover] = Tileset.AVATAR;
            }
        } else if (c == 'S' || c == 's') {
            if (floorTypeList.contains(tiles[Xmover][Ymover - 1])) {
                tiles[Xmover][Ymover--] = Tileset.FLOOR;

                if(tiles[Xmover][Ymover] == Tileset.TELEPORT){
                    int po = getTelePort(Xmover+ Ymover * width);
                    Xmover = po % width;
                    Ymover = po / width;
                }

                tiles[Xmover][Ymover] = Tileset.AVATAR;
            }
        } else if (c == 'A' || c == 'a') {
            if (floorTypeList.contains(tiles[Xmover - 1][Ymover])) {
                tiles[Xmover--][Ymover] = Tileset.FLOOR;

                if(tiles[Xmover][Ymover] == Tileset.TELEPORT){
                    int po = getTelePort(Xmover+ Ymover * width);
                    Xmover = po % width;
                    Ymover = po / width;
                }

                tiles[Xmover][Ymover] = Tileset.AVATAR;
            }
        } else if (c == 'D' || c == 'd') {
            if (floorTypeList.contains(tiles[Xmover + 1][Ymover])) {
                tiles[Xmover++][Ymover] = Tileset.FLOOR;

                if(tiles[Xmover][Ymover] == Tileset.TELEPORT){
                    int po = getTelePort(Xmover+ Ymover * width);
                    Xmover = po % width;
                    Ymover = po / width;
                }
                tiles[Xmover][Ymover] = Tileset.AVATAR;
            }
        }

        if (mountainList.size() > 0 ){
            tiles[mountainList.get(0)%width][mountainList.get(0)/width] = Tileset.MOUNTAIN;
        }

        return true;

    }

    public boolean isAvatarOnMountain(){
        if (mountainList.size() == 0) return false;

        if ( mountainList.get(0) == Xmover + Ymover * width) return true;
        return false;
    }

    private int getTelePort(int startPo)
    {
        int newTelePort = startPo;
        for(int newPo : teleportList) {
            if (newPo != startPo){
                int x = newPo % width;
                int y = newPo / width;

                boolean isFloor = false;
                for (int y1 = y-1; y1 <= y+1; y1++){
                    for (int x1 = x-1; x1 <= x+1; x1++) {
                        if(floorTypeList.contains(tiles[x1][y1])) {
                            newTelePort = x1 + y1* width;
                            isFloor = true;
                            break;
                        }
                    }
                    if(isFloor) break;
                }
                break;
            }
        }

        return newTelePort;
    }

    public boolean addRoomID(String ID, int index) {
        Random rand = new Random();
        rand.setSeed(parseLong(ID));

        int w = (rand.nextInt(6 * index)) % 9 + 4;//min:4, max:12
        int h = (rand.nextInt(6 * index + 1)) % 9 + 4;
        int x = (rand.nextInt(6 * index + 2)) % (width - w - 13) + 6;//min: 6, max:95 +12(w) = 107
        int y = (rand.nextInt(6 * index + 3)) % (height - h - 13) + 6;

        //System.out.println(x + ',' + y +':' + w +'x'+h);

        boolean isEmpty = true;
        int x1 = x - 4, x2 = x + w + 3;
        if (x1 < 3) x1 = 3;
        if (x2 >= width - 6) return false;

        int y1 = y - 3, y2 = y + h + 3;
        if (y1 < 3) y1 = 3;
        if (y2 >= height - 6) return false;

        for (int v = y1; v <= y2; v++) {
            for (int u = x1; u <= x2; u++) {
                if (tiles[u][v] != Tileset.NOTHING) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty == false) break;
        }

        if (!isEmpty) return false;
        for (int Y = y + 1; Y < y + h - 1; Y++) {
            for (int X = x + 1; X < x + w - 1; X++) {
                roomListX.add(X);
                roomListY.add(Y);
            }
        }

        ArrayList<Integer> wallRoom = createRoom(x, y, w, h);

        //light creation
        int xLight = (rand.nextInt(6 * index + 4)) % (w - 2) + x + 1;
        int yLight = (rand.nextInt(6 * index + 5)) % (h - 2) + y + 1;
        lightList.add(xLight + yLight * width);

        int xTele = xLight;
        int yTele = yLight;
        if (rand.nextInt(6*index+1)%3 == 1 && teleportList.size() < 2) {
            xTele = (rand.nextInt(6 * index + 1)) % (w - 2) + x + 1;
            yTele = (rand.nextInt(6 * index + 2)) % (h - 2) + y + 1;
            if (xTele != xLight || yTele != yLight) {
                tiles[xTele][yTele] = Tileset.TELEPORT;
                teleportList.add(xTele + yTele * width);
            }
        }

        if (rand.nextInt(6*index + 2)%3 == 0 && mountainList.size() == 0) {
            int u = (rand.nextInt(6 * index + 1)) % (w - 2) + x + 1;
            int v = (rand.nextInt(6 * index + 2)) % (h - 2) + y + 1;
            if ((u != xLight || v != yLight) &&
                (u != xTele || v != yTele)) {
                tiles[u][v] = Tileset.MOUNTAIN;
                mountainList.add(u + v * width);
            }
        }

        if (nRoom > 1) {
            ArrayList<Integer> wallHall = connectRoomHall(wallRoom);
            wallList.addAll(wallHall);
        }

        wallList.addAll(wallRoom);
        return true;
    }

    public boolean addRandomRoom() {
        Random rand = new Random();

        int X = 0, Y = 0, W = 0, H = 0;
        boolean isDone = false;
        int nTrial = 0;
        while (!isDone && nTrial++ < 20) {
            int w = rand.nextInt(4, 12);
            int h = rand.nextInt(4, 12);
            int x = rand.nextInt(6, width - w - 6);
            int y = rand.nextInt(6, height - h - 6);

            boolean isEmpty = true;
            int x1 = x - 3, x2 = x + w + 3;
            int y1 = y - 3, y2 = y + h + 3;
            for (int v = y1; v <= y2; v++) {
                for (int u = x1; u <= x2; u++) {
                    if (tiles[u][v] != Tileset.NOTHING) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty == false) break;
            }

            if (isEmpty == false) continue;

            X = x;
            Y = y;
            W = w;
            H = h;
            break;
        }

        if (X == 0 && Y == 0 && W == 0 && H == 0) return false;

        for (int y = Y + 1; y < Y + H - 1; y++) {
            roomListX.add(y);
            for (int x = X + 1; x < X + W - 1; x++) {
                roomListY.add(x);
            }
        }


        ArrayList<Integer> wallRoom = createRoom(X, Y, W, H);

        if (nRoom >= 1) {
            ArrayList<Integer> wallHall = connectRoomHall(wallRoom);
            wallList.addAll(wallHall);
        }

        wallList.addAll(wallRoom);
        return true;
    }

    public ArrayList<Integer> createRoom(int x, int y, int w, int h) {
        ArrayList<Integer> walls = new ArrayList<>();
        int x1 = x, x2 = x + w - 1;
        int y1 = y, y2 = y + h - 1;
        for (int v = y1; v <= y2; v++) {
            if (v != y1 && v != y2) {
                walls.add(v * width + x1);
                walls.add(v * width + x2);
            }
            tiles[x1][v] = Tileset.WALL;
            tiles[x2][v] = Tileset.WALL;
        }

        for (int u = x1 + 1; u <= x2 - 1; u++) {
            walls.add(y1 * width + u);
            walls.add(y2 * width + u);
            tiles[u][y1] = Tileset.WALL;
            tiles[u][y2] = Tileset.WALL;
        }


        x1 = x + 1;
        x2 = x + w - 2;
        y1 = y + 1;
        y2 = y + h - 2;
        for (int v = y1; v <= y2; v++)
            for (int u = x1; u <= x2; u++)
                tiles[u][v] = Tileset.FLOOR;

        nRoom++;
        return walls;
    }

    public ArrayList<Integer> connectRoomHall(ArrayList<Integer> wallRoom) {
        int src = -1;
        int dst = -1;
        double minDis = (double) (width * height);

        for (Integer i : wallRoom) {
            int xRoom = i % width;
            int yRoom = i / width;
            if (xRoom < 3 || xRoom >= width - 3 ||
                    yRoom < 3 || yRoom >= height - 3)
                continue;

            for (Integer j : wallList) {
                int xWorld = j % width;
                int yWorld = j / width;

                if (xWorld < 3 || xWorld >= width - 3 ||
                        yWorld < 3 || yWorld >= height - 3)
                    continue;

                int dx = xWorld - xRoom;
                int dy = yWorld - yRoom;
                double dis = Math.sqrt(dx * dx + dy * dy);

                if (minDis >= dis) {
                    boolean isNearDoor = false;
                    for (Integer door : doorWorld) {
                        int xDoor = door % width;
                        int yDoor = door / width;
                        dx = xWorld - xDoor;
                        dy = yWorld - yDoor;
                        if (Math.sqrt(dx * dx + dy * dy) <= 2.0) {
                            isNearDoor = true;
                            break;
                        }
                    }
                    if (!isNearDoor) {
                        minDis = dis;
                        src = i;
                        dst = j;
                    }
                }
            }
        }

        ArrayList<Integer> floors = new ArrayList<>();

        //two doors
        if (dst == -1) return floors;
        int x1 = dst % width;
        int y1 = dst / width;

        tiles[x1][y1] = Tileset.FLOOR;

        doorWorld.add(dst);

        if (tiles[x1 - 1][y1] == Tileset.NOTHING) {
            dst = y1 * width + x1 - 2;
            tiles[x1 - 2][y1] = Tileset.FLOOR;
            floors.add(dst);

            tiles[x1 - 1][y1] = Tileset.FLOOR;
            floors.add(dst + 1);

        } else if (tiles[x1 + 1][y1] == Tileset.NOTHING) {
            dst = y1 * width + x1 + 2;
            tiles[x1 + 2][y1] = Tileset.FLOOR;
            floors.add(dst);

            tiles[x1 + 1][y1] = Tileset.FLOOR;
            floors.add(dst - 1);
        } else if (tiles[x1][y1 - 1] == Tileset.NOTHING) {
            dst = (y1 - 2) * width + x1;
            tiles[x1][y1 - 2] = Tileset.FLOOR;
            floors.add(dst);

            tiles[x1][y1 - 1] = Tileset.FLOOR;
            floors.add(dst + width);
        } else {
            dst = (y1 + 2) * width + x1;
            tiles[x1][y1 + 2] = Tileset.FLOOR;
            floors.add(dst);

            tiles[x1][y1 + 1] = Tileset.FLOOR;
            floors.add(dst - width);
        }

        x1 = src % width;
        y1 = src / width;
        tiles[x1][y1] = Tileset.FLOOR;
        doorWorld.add(src);

        if (tiles[x1 - 1][y1] == Tileset.NOTHING) {
            src = y1 * width + x1 - 2;
            tiles[x1 - 2][y1] = Tileset.FLOOR;
            floors.add(src);

            tiles[x1 - 1][y1] = Tileset.FLOOR;
            floors.add(src + 1);

        } else if (tiles[x1 + 1][y1] == Tileset.NOTHING) {
            src = y1 * width + x1 + 2;
            tiles[x1 + 2][y1] = Tileset.FLOOR;
            floors.add(src);

            tiles[x1 + 1][y1] = Tileset.FLOOR;
            floors.add(src - 1);
        } else if (tiles[x1][y1 - 1] == Tileset.NOTHING) {
            src = (y1 - 2) * width + x1;
            tiles[x1][y1 - 2] = Tileset.FLOOR;
            floors.add(src);

            tiles[x1][y1 - 1] = Tileset.FLOOR;
            floors.add(src + width);
        } else {
            src = (y1 + 2) * width + x1;
            tiles[x1][y1 + 2] = Tileset.FLOOR;
            floors.add(src);

            tiles[x1][y1 + 1] = Tileset.FLOOR;
            floors.add(src - width);
        }

        return drawHall(src, dst, floors);
    }

    public ArrayList<Integer> drawHall(int src, int dst, ArrayList<Integer> floors) {

        int xDst = dst % width;
        int yDst = dst / width;

        int xSrc = src % width;
        int ySrc = src / width;

        int dirX = 1;
        if (xSrc > xDst) dirX = -1;

        for (int x = xSrc; x != xDst; x += dirX) {
            tiles[x][ySrc] = Tileset.FLOOR;
            floors.add(x + ySrc * width);
        }

        int dirY = 1;
        if (ySrc > yDst) dirY = -1;

        for (int y = ySrc; y != yDst; y += dirY) {
            tiles[xDst][y] = Tileset.FLOOR;
            floors.add(xDst + y * width);
        }

        //draw walls
        ArrayList<Integer> walls = new ArrayList<>();
        for (Integer wall : floors) {

            int x1 = wall % width;
            int y1 = wall / width;

            if (tiles[x1][y1] != Tileset.FLOOR) continue;

            for (int y = y1 - 1; y <= y1 + 1; y++) {
                for (int x = x1 - 1; x <= x1 + 1; x++) {
                    if (tiles[x][y] == Tileset.NOTHING) {
                        tiles[x][y] = Tileset.WALL;
                        walls.add(x + y * width);
                    }
                }
            }
        }

        //check corner tile to remove as candidate
        ArrayList<Integer> walls_final = new ArrayList<>();
        for (Integer wall : walls) {

            int x1 = wall % width;
            int y1 = wall / width;

            if (tiles[x1][y1] != Tileset.WALL) continue;

            if ((tiles[x1 - 1][y1] == Tileset.WALL && tiles[x1][y1 - 1] == Tileset.WALL) ||
                    (tiles[x1 - 1][y1] == Tileset.WALL && tiles[x1][y1 + 1] == Tileset.WALL) ||
                    (tiles[x1 + 1][y1] == Tileset.WALL && tiles[x1][y1 + 1] == Tileset.WALL) ||
                    (tiles[x1 + 1][y1] == Tileset.WALL && tiles[x1][y1 - 1] == Tileset.WALL))
                continue;
            ;

            walls_final.add(wall);
        }

        return walls_final;
    }

    public ArrayList<Integer> connectRoomHall_DP(ArrayList<Integer> wallRoom) {
        Random rand = new Random();
        int dst = wallList.get(rand.nextInt(0, wallList.size() - 1));
        int src = wallRoom.get(rand.nextInt(0, wallRoom.size() - 1));

        int x1 = dst % width;
        int y1 = dst / width;
        tiles[x1][y1] = Tileset.FLOOR;

        if (tiles[x1 - 1][y1] == Tileset.NOTHING) dst = y1 * width + x1 - 1;
        else if (tiles[x1 + 1][y1] == Tileset.NOTHING) dst = y1 * width + x1 + 1;
        else if (tiles[x1][y1 - 1] == Tileset.NOTHING) dst = (y1 - 1) * width + x1;
        else dst = (y1 + 1) * width + x1;

        x1 = src % width;
        y1 = src / width;
        tiles[x1][y1] = Tileset.FLOOR;

        if (tiles[x1 - 1][y1] == Tileset.NOTHING) src = y1 * width + x1 - 1;
        else if (tiles[x1 + 1][y1] == Tileset.NOTHING) src = y1 * width + x1 + 1;
        else if (tiles[x1][y1 - 1] == Tileset.NOTHING) src = (y1 - 1) * width + x1;
        else src = (y1 + 1) * width + x1;

        ArrayList<Integer> path = DP(src, dst);
        ArrayList<Integer> wallHall = new ArrayList<Integer>();

        for (int i = 0; i < path.size(); i++) {
            int curX = path.get(i) % width;
            int curY = path.get(i) / width;

            tiles[curX][curY] = Tileset.FLOOR;
            continue;

            /*int nexX = path.get(i + 1) % width;
            int nexY = path.get(i + 1) / width;
            int dx = nexX - curX;
            int dy = nexY - curY;
            if (dx == -1 && dy == 1) wallHall.addAll(setLH(curX, curY, false));
            else if (dx == 0 && dy == 1) wallHall.addAll(setHV(curX, curY, true));
            else if (dx == 1 && dy == 1) wallHall.addAll(setLH(curX, curY, true));
            else if (dx == -1 && dy == 0) wallHall.addAll(setHH(curX, curY, false));
            else if (dx == 1 && dy == 0) wallHall.addAll(setHH(curX, curY, true));
            else if (dx == -1 && dy == -1) wallHall.addAll(setLH(curX, curY, false));
            else if (dx == 0 && dy == -1) wallHall.addAll(setHV(curX, curY, false));
            else wallHall.addAll(setLH(curX, curY, true));*/
        }
        return wallHall;
    }

    public TETile[][] getMap() {
        return tiles;
    }

    public ArrayList<Integer> DP(int srcPo, int desPo) {
        ArrayList<Double> cost = new ArrayList<>();
        int size = width * height;
        for (int i = 0; i < size; i++) cost.add((double) size);

        HashMap<Integer, Double> neiList = new HashMap<>();
        neiList.put(srcPo, 0.0);

        int maxCost = width * 2;
        int nowCost = 0;

        while (neiList.size() > 0 && nowCost < maxCost) {
            HashMap<Integer, Double> new_neiList = new HashMap<>();

            for (HashMap.Entry<Integer, Double> nei : neiList.entrySet()) {
                int x = nei.getKey() % width;
                int y = nei.getKey() / width;
                if (tiles[x][y] != Tileset.NOTHING) continue;

                cost.set(nei.getKey(), nei.getValue());
                double refCost = cost.get(nei.getKey());

                for (int x1 = x - 1; x1 <= x + 1; x1++)
                    if (x1 >= 2 && x1 < width - 3)
                        for (int y1 = y - 1; y1 <= y + 1; y1++)
                            if (y1 >= 2 && y1 < height - 3) {
                                int po = x1 + y1 * width;
                                if (cost.get(po) == size && tiles[x1][y1] == Tileset.NOTHING) {
                                    double curCost = refCost + Math.sqrt((x1 - x) * (x1 - x) + (y1 - x) * (y1 - x));

                                    if (new_neiList.containsKey(po)) {
                                        if (new_neiList.get(po) > curCost) {
                                            new_neiList.put(po, curCost);
                                        }
                                    } else new_neiList.put(po, curCost);
                                }

                            }

            }
            neiList = (HashMap<Integer, Double>) new_neiList.clone();
            nowCost++;
        }
        return trackBackPath(srcPo, desPo, cost);
    }

    private ArrayList<Integer> trackBackPath(int srcPo, int desPo, ArrayList<Double> cost) {
        ArrayList<Integer> path = new ArrayList<>();
        path.add(desPo);
        int curPo = desPo;
        while (curPo != srcPo) {
            int x1 = curPo % width - 2;
            int y1 = curPo / width - 2;
            int x2 = x1 + 5;
            int y2 = y1 + 5;
            int minPo = curPo;
            double minCost = cost.get(curPo);

            for (int y = y1; y <= y2; y++)
                for (int x = x1; x <= x2; x++) {
                    if (cost.get(x + y * width) < minCost) {
                        minCost = cost.get(x + y * width);
                        minPo = x + y * width;
                    }
                }
            if (cost.get(curPo) == minCost) break;
            path.add(minPo);
            curPo = minPo;
        }

        return path;

    }

    private ArrayList<Integer> setLH(int x, int y, boolean isRight) {
        ArrayList<Integer> wallHall = new ArrayList<>();

        int lefX = x - 1, rightX = x + 1;
        if (x < 1 || x >= width - 2 || y < 1 || y >= height - 2)
            return wallHall;

        //for (int y1 = y; y1 <= y + 1; y1++)
        //    for (int x1 = lefX; x1 <= rightX; x1++)
        //        if (tiles[x1][y1] != Tileset.NOTHING)
        //            return wallHall;

        //for (int y1 = y; y1 <= y + 1; y1++)
        //for (int x1 = lefX; x1 <= rightX; x1++)
        //        tiles[x1][y1] = Tileset.WALL;

        tiles[x][y] = Tileset.FLOOR;

        if (isRight) tiles[x + 1][y] = Tileset.FLOOR;
        else tiles[x - 1][y] = Tileset.FLOOR;

        //for (int y1 = y; y1 <= y + 1; y1++)
        //for (int x1 = lefX; x1 <= rightX; x1++){
        //     if (tiles[x1][y1] == Tileset.WALL)
        //        wallHall.add(x1+y1*width);
        //}

        return wallHall;
    }

    private ArrayList<Integer> setHV(int x, int y, boolean isUP) {
        ArrayList<Integer> wallHall = new ArrayList<>();
        int lefX = x - 1, rightX = x + 1;
        if (x < 2 || x >= width - 3 || y < 2 || y >= height - 3)
            return wallHall;

        if (isUP) {
            //for (int y1 = y; y1 <= y+1; y1++)
            /// for (int x1 = lefX; x1 <= rightX; x1++){
            //     if (tiles[x1][y1] != Tileset.NOTHING) return wallHall;
            // }

            tiles[x][y] = Tileset.FLOOR;
            //tiles[lefX][y] = Tileset.WALL;
            //tiles[rightX][y] = Tileset.WALL;

            //for (int y1 = y; y1 <= y + 1; y1++)
            //for (int x1 = lefX; x1 <= rightX; x1++){
            //    if (tiles[x1][y1] == Tileset.WALL)
            //        wallHall.add(x1+y1*width);
            //}

        } else {
            //down
            //for (int y1 = y; y1 >= y - 1; y1--)
            //    for (int x1 = lefX; x1 <= rightX; x1++) {
            //         if (tiles[x1][y1] != Tileset.NOTHING) return wallHall;
            //    }

            tiles[x][y] = Tileset.FLOOR;
            //tiles[lefX][y] = Tileset.WALL;
            //tiles[rightX][y] = Tileset.WALL;

            //for (int y1 = y; y1 >= y - 1; y1--)
            //for (int x1 = lefX; x1 <= rightX; x1++){
            //    if (tiles[x1][y1] == Tileset.WALL)
            //        wallHall.add(x1+y1*width);
            //}
        }

        return wallHall;
    }

    private ArrayList<Integer> setHH(int x, int y, boolean isRight) {
        ArrayList<Integer> wallHall = new ArrayList<>();
        int up = y + 1, down = y - 1;
        if (x < 2 || x >= width - 3 || y < 2 || y >= height - 3)
            return wallHall;

        if (isRight) {
            //for (int x1 = x; x1 <= x+1; x1++)
            //for (int y1 = down; y1 <= up; y1++){
            //    if (tiles[x1][y1] != Tileset.NOTHING) return wallHall;
            //}

            tiles[x][y] = Tileset.FLOOR;
            //tiles[x][down] = Tileset.WALL;
            //tiles[x][up] = Tileset.WALL;

            //for (int x1 = x; x1 <= x+1; x1++)
            //for (int y1 = down; y1 <= up; y1++){
            //    if (tiles[x1][y1] == Tileset.WALL) wallHall.add(x1+y1*width);
            //}

        } else {
            //left
            //for (int x1 = x; x1 >= x - 1; x1--)
            //for (int y1 = down; y1 <= up; y1++) {
            //    if (tiles[x1][y1] != Tileset.NOTHING) return wallHall;
            //}

            tiles[x][y] = Tileset.FLOOR;
            //tiles[x][down] = Tileset.WALL;
            //tiles[x][up] = Tileset.WALL;

            //for (int x1 = x; x1 >= x - 1; x1--)
            //for (int y1 = down; y1 <= up; y1++)
            // {
            //     if (tiles[x1][y1] == Tileset.WALL) wallHall.add(x1+y1*width);
            // }
        }

        return wallHall;
    }

    public boolean onLight(char rgb, int xMover, int yMover) {
        boolean isOn = false;
        for (int po : lightList) {
            int x = po % width;
            int y = po / width;

            if (x != xMover || y != yMover) continue;

            if (lightOffList.contains(po) == true) isOn = true;

            for (int y1 = y - 3; y1 <= y + 3; y1++) {
                if (y1 >= 1 && y1 < height - 1)
                    for (int x1 = x - 3; x1 <= x + 3; x1++) {
                        if (x1 < 1 && x1 >= width - 1) continue;
                        if (floorTypeList.contains(tiles[x1][y1]) == false ||
                            tiles[x1][y1] == Tileset.TELEPORT) {
                            continue;
                        }

                        int dx = x1 - x;
                        int dy = y1 - y;
                        int dis = Math.max(Math.abs(dx), Math.abs(dy));

                        TETile light = null;
                        if (rgb == 'b') {
                            if (dis == 0) light = Tileset.LIGHT0_B;
                            else if (dis == 1) light = Tileset.LIGHT1_B;
                            else if (dis == 2) light = Tileset.LIGHT2_B;
                            else if (dis == 3) light = Tileset.LIGHT3_B;
                        } else if(rgb == 'g') {
                            if (dis == 0) light = Tileset.LIGHT0_G;
                            else if (dis == 1) light = Tileset.LIGHT1_G;
                            else if (dis == 2) light = Tileset.LIGHT2_G;
                            else if (dis == 3) light = Tileset.LIGHT3_G;
                        }
                        else {
                            if (dis == 0) light = Tileset.LIGHT0_R;
                            else if (dis == 1) light = Tileset.LIGHT1_R;
                            else if (dis == 2) light = Tileset.LIGHT2_R;
                            else if (dis == 3) light = Tileset.LIGHT3_R;
                        }


                        if (light != null) tiles[x1][y1] = light;

                    }
            }

            for (int i = 0; i < lightOffList.size(); i++) {
                if (lightOffList.get(i) == po) lightOffList.remove(i);
            }

            break;

        }

        return isOn;
    }

    void onLights(char rgb) {
        for (int po : lightList) {
            int x = po % width;
            int y = po / width;


            for (int y1 = y - 3; y1 <= y + 3; y1++) {
                if (y1 >= 1 && y1 < height - 1)
                    for (int x1 = x - 3; x1 <= x + 3; x1++) {
                        if (x1 < 1 && x1 >= width - 1) continue;
                        if (tiles[x1][y1] != Tileset.FLOOR) continue;

                        int dx = x1 - x;
                        int dy = y1 - y;
                        int dis = Math.max(Math.abs(dx), Math.abs(dy));

                        TETile light = null;
                        if (rgb == 'b') {
                            if (dis == 0) light = Tileset.LIGHT0_B;
                            else if (dis == 1) light = Tileset.LIGHT1_B;
                            else if (dis == 2) light = Tileset.LIGHT2_B;
                            else if (dis == 3) light = Tileset.LIGHT3_B;
                        } else if(rgb == 'g') {
                            if (dis == 0) light = Tileset.LIGHT0_G;
                            else if (dis == 1) light = Tileset.LIGHT1_G;
                            else if (dis == 2) light = Tileset.LIGHT2_G;
                            else if (dis == 3) light = Tileset.LIGHT3_G;
                        }
                        else {
                            if (dis == 0) light = Tileset.LIGHT0_R;
                            else if (dis == 1) light = Tileset.LIGHT1_R;
                            else if (dis == 2) light = Tileset.LIGHT2_R;
                            else if (dis == 3) light = Tileset.LIGHT3_R;
                        }

                        if (light != null) tiles[x1][y1] = light;

                    }
            }
        }

        for (int po : lightOffList) {
            offLight(po % width, po / width);
        }
    }

    public boolean onLight(char color)
    {
        if (lightOffList.size() == 0) return false;

        int po = lightOffList.get(0);
         return onLight(color, po%width, po/width);
    }
    public boolean offLight(int xMover, int yMover) {
        boolean isOff = false;
        for (int po : lightList) {
            int x = po % width;
            int y = po / width;

            if (xMover != x || yMover != y) continue;

            if (lightOffList.contains(po) == false) {
                lightOffList.add(po);
                isOff = true;
            }

            for (int y2 = y - 3; y2 <= y + 3; y2++) {
                if (y2 >= 1 && y2 < height - 1) {
                    for (int x2 = x - 3; x2 <= x + 3; x2++) {
                        if (x2 < 1 && x2 >= width - 1) continue;
                        if (lightTypeList.contains(tiles[x2][y2])) {
                            if ( tiles[x2][y2] != Tileset.TELEPORT)
                                tiles[x2][y2] = Tileset.FLOOR;
                        }

                        TETile light = null;
                        int dx = x2 - x;
                        int dy = y2 - y;
                        int dis = Math.max(Math.abs(dx), Math.abs(dy));

                        if (dis == 0) light = Tileset.LIGHT0_G;
                    }
                }
            }

            tiles[x][y] = Tileset.LIGHT0_B;
            break;
        }


        return isOff;
    }

        void offLights ()
        {
            for (int po : lightList) {
                int x = po % width;
                int y = po / width;

                for (int y1 = y - 3; y1 <= y + 3; y1++) {
                    if (y1 >= 1 && y1 < height - 1)
                        for (int x1 = x - 3; x1 <= x + 3; x1++) {
                            if (x1 < 1 && x1 >= width - 1) continue;
                            if (lightTypeList.contains(tiles[x1][y1])) tiles[x1][y1] = Tileset.FLOOR;
                        }
                }

                tiles[x][y] = Tileset.LIGHT0_B;
            }
        }

    }



