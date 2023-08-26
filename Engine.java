package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static edu.princeton.cs.algs4.StdDraw.mouseX;
import static edu.princeton.cs.algs4.StdDraw.mouseY;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 60;

    private static final char[] OPTIONS = {'N', 'L', 'Q'};

    private static boolean gameOver = false;
    private static String _argInput = "";

    private static TETile[][] currWorld;
    private static int mouseX = 0, mouseY = 0, isOff =0, first = 0;

    private static boolean isOn = false;
    private  static char lightAll = 'b' ;

    private static int colonDone = 0;

    private static boolean idsplayMenu = false;

    genRoomPath mapGlobal;

    Map<String, TETile[][]> worldList;

    public Engine() {
        currWorld = null;
        worldList = new HashMap<String, TETile[][]>();
        currWorld = new TETile[WIDTH][HEIGHT];
        mapGlobal = new genRoomPath(currWorld);
        isOn = false;
    }

    //initialize all tiles as nothing
    public static void init2D(TETile[][] tiles) {

        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height ; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }

        Engine engine = new Engine();
        engine.interactWithInputString("");

    }


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */


    public void interactWithKeyboard() {

        ter.initialize(WIDTH, HEIGHT);
        _argInput = "";
        isOff = 0;
        gameOver = false;
        colonDone = 0;

        long gameTimeMax = 120;
        long gameStartTime = 0;
        long gameTime = 0;

        displayMenu();
        Boolean isDispalyAll = false;
        boolean isKeySwitch = false;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm");
        String dateTime = now.format(format);

        while (!gameOver) {
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);

            if (gameStartTime != 0) {
                gameTime = System.currentTimeMillis() / 1000 - gameStartTime;
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.filledRectangle(WIDTH / 2, 1.5, WIDTH / 2, 1.5);

                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(0, 2, "  Game Remained Time[Sec]:" + (gameTimeMax - gameTime));
                StdDraw.show();
                if (gameTime > gameTimeMax) {
                    gameOver = true;

                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    Font font = new Font("Monaco", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "YOU LOST!");
                    StdDraw.show();
                    StdDraw.pause(2000);

                }
            }

            if (!idsplayMenu) {
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.filledRectangle(WIDTH / 2, HEIGHT - 1.5, WIDTH / 2, 1.5);
                Font font = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(font);

                StdDraw.setPenColor(Color.WHITE);
                StdDraw.line(0, HEIGHT - 3, WIDTH, HEIGHT - 3);
                StdDraw.textLeft(0, HEIGHT - 2, "        " + "Current date and time: " + dateTime + "       "
                        + "Type: " + currWorld[(int) mouseX()][(int) mouseY()].description()
                        + "   " + "Lights On: " + (mapGlobal.nRoom + isOff));
                StdDraw.show();
            }

            Font fontSmall1 = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall1);


            if (StdDraw.hasNextKeyTyped()) {
                char inputs = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (inputs == 'N' || inputs == 'L' || inputs == 'Q' || inputs == ':' ) {

                    typeFunction(inputs);
                    if(inputs == 'Q' && gameStartTime == 0) System.exit(0);
                    if(inputs != 'Q')gameStartTime = System.currentTimeMillis() / 1000;

                    currWorld = mapGlobal.getMap();
                    ter.renderFrame(currWorld);
                } else if (currWorld != null
                        && (inputs == 'W' || inputs == 'A' || inputs == 'S'
                        || inputs == 'D' || inputs == 'O'
                        || inputs == 'T' || inputs == 'C')) {

                    if (inputs == 'C') {
                        if (isKeySwitch) isKeySwitch = false;
                        else isKeySwitch = true;
                    }

                    if (inputs == 'W' || inputs == 'A' || inputs == 'S' || inputs == 'D') {
                        if (isKeySwitch == false) mapGlobal.runCommand(inputs);
                        else mapGlobal.runCommandSwitch(inputs);
                    }

                    if (mapGlobal.isAvatarOnMountain()) {
                        if (mapGlobal.onLight(lightAll)) isOff++;
                    }

                    mapGlobal.onLights(lightAll);

                    if (inputs == 'T' || inputs == 't') {
                        if (mapGlobal.offLight(mapGlobal.getXmover(), mapGlobal.getYmover())) isOff--;
                        else if (mapGlobal.onLight(lightAll, mapGlobal.getXmover(), mapGlobal.getYmover())) isOff++;
                    }


                    currWorld = mapGlobal.getMap();

                    if (isDispalyAll && (inputs == 'O' || inputs == 'o')) {
                        ter.renderFrame(currWorld);
                        isDispalyAll = false;
                    } else if (!isDispalyAll && (inputs == 'O' || inputs == 'o')) {
                        ter.renderFrameLocal(currWorld, mapGlobal.getXmover(), mapGlobal.getYmover());
                        isDispalyAll = true;
                    } else {
                        if (isDispalyAll) ter.renderFrameLocal(currWorld, mapGlobal.getXmover(), mapGlobal.getYmover());
                        else ter.renderFrame(currWorld);
                    }

                    _argInput += String.valueOf(inputs);

                    if (mapGlobal.nRoom + isOff == 0) {
                        gameOver = true;

                        StdDraw.clear(Color.BLACK);
                        StdDraw.setPenColor(Color.WHITE);
                        Font font = new Font("Monaco", Font.BOLD, 30);
                        StdDraw.setFont(font);
                        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "YOU WON!");
                        StdDraw.show();
                        StdDraw.pause(2000);
                    }
                }

                mouseX = (int) mouseX();
                mouseY = (int) mouseY();

            }
        }

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "GAME OVER!");
        StdDraw.show();
        StdDraw.pause(1000);

        interactWithKeyboard();
    }



    public void displayMenu() {

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "Main Menu");

        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontSmall);
        double yPos = HEIGHT / 2.0;
        for (char option : OPTIONS) {
            StdDraw.text(WIDTH / 2.0, yPos, type(option));
            yPos -= 2;
        }
        idsplayMenu = true;
        StdDraw.show();
    }

    public String type(char option) {
        switch (option) {
            case 'N':
                return "New Game (N)";
            case 'L':
                return "Log Game (L)";
            case 'Q':
                return "Quit (Q)";
            default:
                return "";
        }
    }

    private void typeFunction(char option) {
        switch (option) {
            case 'N':
                _argInput = startNewWorld();
                idsplayMenu = false;

                break;
            case 'L':
                _argInput = loadFileCommand("map.txt");
                currWorld = interactInput(_argInput);
                idsplayMenu = false;
                break;
            case 'Q':
                if(colonDone == 1) {
                    writeFileCommand("map.txt", _argInput);

                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    Font font = new Font("Monaco", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "WORLD SAVED!");
                    StdDraw.show();
                    StdDraw.pause(1000);

                    gameOver = true;

                    System.exit(0);
                    break;
                } else if (mapGlobal.wallList == null && colonDone != 1){
                    gameOver = true;
                    System.exit(0);
                    break;
                } else {
                    break;
                }


            case ':':
                    colonDone = 1;
                    break;
            default:
                return;
        }
    }


    private TETile[][] interactInput(String argInput) {
        TERenderer t = new TERenderer();
        t.initialize(WIDTH, HEIGHT);
        TETile[][] world = null; //new TETile[WIDTH][HEIGHT];

        String id = getID(argInput);
        if (id.compareTo("") == 0) {
            return world;
        }

        System.out.println("id:" + id);

        /////Map generation
        init2D(currWorld);
        mapGlobal = new genRoomPath(currWorld);

        int n = Integer.parseInt(String.valueOf(id.charAt(0) + id.charAt(1)));
        for (int i = 1; i < n; i++) {
            mapGlobal.addRoomID(id, i);
        }

        //run commands
        mapGlobal.onLights(lightAll);
        String command = getCommand(argInput, id);
        for (int i = 0; i < command.length(); i++) {
            char input = Character.toUpperCase(command.charAt(i));

            if (input == 'W' || input == 'A' || input == 'S' || input == 'D') {
                mapGlobal.runCommand(input);
                if(mapGlobal.isAvatarOnMountain()){
                    mapGlobal.onLight(lightAll);
                    isOff++;
                }
            }
            else  if (input == 'T') {
                if(isOn == false) {
                    isOn = true;
                    if(mapGlobal.offLight(mapGlobal.getXmover(), mapGlobal.getYmover())) isOff--;
                } else if (isOn == true) {
                    isOn = false;
                    if(mapGlobal.onLight(lightAll, mapGlobal.getXmover(), mapGlobal.getYmover()))isOff++;
                }
                first++;
            }
        }

        mapGlobal.onLights(lightAll);



        return mapGlobal.getMap();

    }

    private void writeFileCommand(String fileName, String argInput) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(argInput);
            writer.close();
        } catch (IOException e) {
            System.out.println("writing " + fileName + " file is error");
        }
    }

    private String loadFileCommand(String fileName) {
        String command = "";
        try {
            command = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            System.out.println("reading " + fileName + " file is error");
        }

        return command;
    }


    private String startNewWorld() {
        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontSmall);
        int yPos = HEIGHT / 2 - 8;
        StdDraw.text(WIDTH / 2.0, yPos, "Enter seed (press S to start):");
        StdDraw.show();

        String seedInput = "";
        int yPosSeed = HEIGHT / 2 - 10;

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'S' || key == 's') {
                    break; //START
                } else if (Character.isDigit(key))
                    seedInput += key; //Add to Display
            }

            StdDraw.clear(Color.BLACK);
            StdDraw.text(WIDTH / 2.0, yPos, "Enter seed (press S to start): " + seedInput);
            StdDraw.show();
        }

        //create a new wrold
        int n = Integer.parseInt(String.valueOf(seedInput.charAt(0) + seedInput.charAt(1)));
        init2D(currWorld);
        mapGlobal = new genRoomPath(currWorld);
        for (int i = 1; i < n; i++) {
            mapGlobal.addRoomID(seedInput, i);
            ter.renderFrame(mapGlobal.getMap());
        }

        mapGlobal.runCommand(' ');
        mapGlobal.onLights(lightAll);
        currWorld = mapGlobal.getMap();

        return 'N' + seedInput;
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        if (input != "") {
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            init2D(world);
            genRoomPath map1 = new genRoomPath(world);

            TERenderer t = new TERenderer();
            t.initialize(WIDTH, HEIGHT);


            if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
                String id = getID(input);
                if (id.compareTo("") == 0) {
                    return world;
                }

                String itrStr = id.substring(0, 2);
                int n = Integer.parseInt(String.valueOf(itrStr));
                n += n;


                for (int i = 1; i < n; i++) {
                    map1.addRoomID(id, i);
                }

                for (int i = id.length() + 1; i < input.length(); i++) {
                    if (i == id.length() + 1 && input.charAt(i) == 's') {
                        continue;
                    }
                    if (input.charAt(i) == 'W' || input.charAt(i) == 'w' || input.charAt(i) == 'A'
                            || input.charAt(i) == 'a'
                            || input.charAt(i) == 'S' || input.charAt(i) == 's' || input.charAt(i) == 'D'
                            || input.charAt(i) == 'd') {
                        map1.onLights('r');
                        map1.runCommand(input.charAt(i));
                    } else if (input.charAt(i) == ':' && i + 1 < input.length()
                            && (input.charAt(i + 1) == 'Q' || input.charAt(i + 1) == 'q')) {
                        writeFileCommand("map.txt", input.substring(0, input.length() - 2));
                    }
                }

            } else if (input.charAt(0) == 'l' || input.charAt(0) == 'l') {
                String command = loadFileCommand("map.txt");
                command += input.substring(1, input.length());
                world = interactWithInputString(command);
                writeFileCommand("map.txt", command);

            }

            world = map1.getMap();
            t.renderFrame(world);

            return world;


        } else {
            interactWithKeyboard();
            return currWorld;
        }
    }


    static String getID(String input) {
        String idInput = "";
        boolean isStartDigit = false;
        for (int index = 1; index < input.length(); index++) {
            if (input.charAt(index) >= '0'
                    && input.charAt(index) <= '9') {
                idInput = idInput + input.charAt(index);
                isStartDigit = true;
            } else if (!isStartDigit) {
                break;
            }
        }
        return idInput;
    }


    void writeIDmap(String mapID, TETile[][] world) {
        worldList.put(mapID, world);
    }

    TETile[][] loadIDmap(String mapID) {
        if (!worldList.containsKey(mapID)) {
            return null;
        }
        return worldList.get(mapID);
    }


    void writeFileWorld(String mapID) {
        try {
            ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(mapID));
            str.writeObject(currWorld);
        } catch (IOException | ClassCastException excp) {
            System.out.println("wrting " + mapID + " file is error");
        }
    }


   void loadFileWorld(String mapID) {
       File inFile = new File(mapID);
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            currWorld = (TETile[][]) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            System.out.println(mapID + " file is not existed");
        }
    }

     String getCommand(String input, String id) {
        String command ="";
        int index = id.length();
        while(input.length() > index && input.charAt(index) != ':'){
            command = command + input.charAt(index++);
        }

        return command;
    }

    String getCommand(String input) {
        String command ="";
        int index = 1;
        while(input.length() > index && input.charAt(index) != ':'){
            command = command + input.charAt(index);
        }

        return command;
    }



}
