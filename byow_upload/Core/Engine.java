package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    private Random rand;
    private List<Position> isFloor; // do I need hashCode method for Position class? Have .equals
    private Map<Position, Position> addedRooms;
    private int numberOfRooms;
    private List<Room> listOfRooms;
    private WeightedQuickUnionUF connectedRooms;
    private HashMap<Room, Integer> roomIndex;
    private Position aPos;
    private String aName;
    private boolean on;

    private static class Position {
        private int x;
        private int y;

        private Position(int xPos, int yPos) {
            x = xPos;
            y = yPos;
        }

        public boolean equals(Position a, Position b) {
            if (a.x == b.x && a.y == b.y) {
                return true;
            }
            return false;
        }
    }

    public class Room {
        Position lowerLeft;
        Position upperRight;
        int width;
        int height;

        public Room(Position lowerLeft, Position upperRight) {
            this.lowerLeft = lowerLeft;
            this.upperRight = upperRight;
            width = Math.abs(lowerLeft.x - upperRight.x);
            height = Math.abs(lowerLeft.y - upperRight.y);
        }

        public Position getRandomInRange() {
            int xVal = Math.abs(rand.nextInt() % this.width);
            int yVal = Math.abs(rand.nextInt() % this.height);
            int xCoord = xVal + lowerLeft.x;
            int yCoord = yVal + lowerLeft.y;
            return new Position(xCoord, yCoord);
        }
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        on = false;
        aName = nameTyper();
        drawMenu();
        String seed = seedTyper();
        TETile[][] world;
        ter.initialize(WIDTH, HEIGHT + 3);


        StdDraw.setPenColor(Color.WHITE);


        //everything after seed
        boolean flag = true;
        while (flag) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                seed += c;
            }
            world = interactWithInputString(seed);
            StdDraw.enableDoubleBuffering();
            if (on) {
                limeLight(4, world);
            }
            StdDraw.setPenColor(Color.WHITE);
            String thing = valueOfCursor(world);
            StdDraw.text(9, HEIGHT, thing);
            StdDraw.text(5, HEIGHT,"HUD");
            StdDraw.text(WIDTH / 2, HEIGHT, aName);
            StdDraw.show();
            ter.renderFrame(world);
        }
    }

    public String seedTyper() {
        String seed = "";
        boolean flag = true;
        boolean isNotL = true;
        while (flag) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'n' || c == 'N' || c == 'l' || c == 'L') {
                    flag = false;
                    seed += c;
                    if (c == 'L' || c == 'l') {
                        isNotL = false;
                    }
                }
            }
        }
        StdDraw.clear(Color.PINK);
        drawFrame("Enter Seed (S):", WIDTH / 2, HEIGHT / 2 + 10);
        System.out.println("pass");
        while (isNotL) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.clear(Color.PINK);
                drawFrame("Enter Seed (S):", WIDTH / 2, HEIGHT / 2 + 10);
                char c = StdDraw.nextKeyTyped();
                seed += c;
                if (c == 'S' || c == 's') {
                    break;
                }
                drawFrame(seed.substring(1), WIDTH / 2, HEIGHT / 2);
            }
        }
        return seed;
    }

    private void moveAvatar(char c, TETile[][] tiles) {
        int xMove = 0;
        int yMove = 0;
        if (c == 'd' || c == 'D') {
            xMove = 1;
        } else if (c == 'w' || c == 'W') {
            yMove = 1;
        } else if (c == 'a' || c == 'A') {
            xMove = -1;
        } else if (c == 's' || c == 'S') {
            yMove = -1;
        }
        Position newPosition = new Position(aPos.x + xMove, aPos.y + yMove);
        if (checkForWall(newPosition, tiles)) {
            return;
        }
        clearAvatar(tiles);
        aPos = newPosition;
        drawAvatar(tiles);
    }

    //returns true if there is a wall
    private boolean checkForWall(Position p, TETile[][] tiles) {
        if (tiles[p.x][p.y] == Tileset.WALL) {
            return true;
        }
        return false;
    }
    private void drawAvatar(TETile[][] tiles) {
        tiles[aPos.x][aPos.y] = Tileset.FLOWER;
    }

    private void clearAvatar(TETile[][] tiles) {
        tiles[aPos.x][aPos.y] = Tileset.GRASS;
    }

    private void drawMenu() {
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.PINK);
        StdDraw.enableDoubleBuffering();


        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Main Menu");


        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 10, "Flower Boi");



        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "New World (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Load (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Quit (Q)");

        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(WIDTH / 2, 2, "WELCOME " + aName.toUpperCase());


        StdDraw.show();
    }

    private String nameTyper() {
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.PINK);
        StdDraw.enableDoubleBuffering();

        drawFrame("NAME? (.)", WIDTH / 2, HEIGHT / 2 + 10);

        String name = "";
        boolean flag = true;
        int count = 0;
        while (flag) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.clear(Color.PINK);
                drawFrame("NAME? (.)", WIDTH / 2, HEIGHT / 2 + 10);
                char c = StdDraw.nextKeyTyped();
                if (c == '.') {
                    break;
                }
                count++;
                name += c;
                drawFrame(name, WIDTH / 2, HEIGHT / 2);
            }
            StdDraw.show();
        }
        return name;
    }

    private void drawFrame(String word, int w, int h) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(w, h, word);
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        String saveData = input;
        TETile[][] finalWorldFrame;
        String actions;
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            rand = new Random(parseInput(input));
            isFloor = new ArrayList<>();
            addedRooms = new HashMap<>();
            numberOfRooms = 0;
            listOfRooms = new ArrayList<>();

            finalWorldFrame = new TETile[WIDTH][HEIGHT + 3];
            initializeArena(finalWorldFrame);

            int maxNumberOfRooms = (WIDTH + HEIGHT) / 4;

            for (int i = 0; i < maxNumberOfRooms; i++) {
                drawRoom(randPosition(), finalWorldFrame);
            }
            roomIndex = new HashMap<>();
            connectedRooms = new WeightedQuickUnionUF(numberOfRooms);
            for (int i = 0; i < numberOfRooms; i++) {
                roomIndex.put(listOfRooms.get(i), i);
            }

            connectAllRooms(finalWorldFrame);

            placeWalls(finalWorldFrame);

            //place avatar
            int index = randomBound(0, isFloor.size());
            aPos = isFloor.get(index);
            drawAvatar(finalWorldFrame);

            //avatar moving
            actions = afterSeed(input);
        } else {
            finalWorldFrame = interactWithInputString(loadSeed());
            actions  = input.substring(1);
            saveData  = loadSeed() + actions;
        }
        for (int i = 0; i < actions.length(); i++) {
            char c = actions.charAt(i);
            if (c == 'w' || c == 'd' || c == 's' || c == 'a'
                || c == 'W' || c == 'D' || c == 'S' || c == 'A') {
                moveAvatar(c, finalWorldFrame);
            }
            if (c == 'z') {
                on = !on;
            }
            if (c == ':' && i + 1 != actions.length()) {
                char c2 = actions.charAt((i + 1));
                if (c2 == 'q' || c2 == 'Q') {
                    //quit and save
                    saveData = saveData.substring(0, saveData.length() - 2);
                    saver(saveData);
                    System.exit(0);
                }
            }
        }

        return finalWorldFrame;
    }


    private void saver(String state) {

        File f = new File("saveData.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(state);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private static String loadSeed() {
        File f = new File("saveData.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (String) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        return "";
    }

    // Logistics


    private int randomBound(int lower, int higher) {
        int r = RandomUtils.uniform(rand, higher - lower) + lower;
        return r;
    }

    //returns everything after seed
    private String afterSeed(String input) {
        while (input.charAt(0) != 'S' && input.charAt(0) != 's') {
            input = input.substring(1);
        }
        input = input.substring(1);
        return input;
    }

    private Long parseInput(String input) {
        String ret = "";
        input = input.substring(1);
        while (input.charAt(0) != 'S' && input.charAt(0) != 's') {
            ret = ret + input.charAt(0);
            input = input.substring(1);
        }
        Long seed = Long.parseLong(ret);
        return seed;
    }

    private void initializeArena(TETile[][] tiles) {
        //TERenderer tera = new TERenderer();
        //tera.initialize(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT + 3; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        //return tera;
    }

    // Drawing
    private void drawRoom(Position pos, TETile[][] tiles) {
        int w = randomBound(4, 10);
        int h = randomBound(4, 10);

        Position boxPosition = new Position(pos.x, pos.y);
        if (validate(boxPosition, w, h)) {
            return;
        }
        numberOfRooms += 1;
        addedRooms.put(boxPosition, new Position(boxPosition.x + w, boxPosition.y + h));
        listOfRooms.add(new Room(boxPosition, new Position(boxPosition.x + w, boxPosition.y + h)));
        drawBox(boxPosition, w, h, tiles, Tileset.SAND);
    }

    private boolean allConnected() {
        for (int i = 0; i < numberOfRooms; i++) {
            for (int j = i + 1; j < numberOfRooms; j++) {
                if (!connectedRooms.connected(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void connectAllRooms(TETile[][] world) {
        if (numberOfRooms % 2 == 0) {
            int halfRooms = numberOfRooms / 2;
            for (int i = 0; i < halfRooms; i++) {
                connectTwoRooms(listOfRooms.get(i), listOfRooms.get(i + halfRooms), world);
                connectedRooms.union(i, i + halfRooms);
            }
        }
        if (numberOfRooms % 2 == 1) {
            int halfRooms = numberOfRooms / 2;
            for (int i = 0; i < halfRooms; i++) {
                connectTwoRooms(listOfRooms.get(i), listOfRooms.get(i + halfRooms), world);
                connectedRooms.union(i, i + halfRooms);
            }
            connectTwoRooms(listOfRooms.get(numberOfRooms - 1), listOfRooms.get(1), world);
            connectedRooms.union(numberOfRooms - 1, 1);
        }
        if (!allConnected()) {
            if (numberOfRooms % 2 == 0) {
                for (int i = 0; i < numberOfRooms; i += 2) {
                    connectTwoRooms(listOfRooms.get(i), listOfRooms.get(i + 1), world);
                    connectedRooms.union(i, i + 1);
                }
            }
            if (numberOfRooms % 2 == 1) {
                for (int i = 0; i < numberOfRooms - 1; i += 2) {
                    connectTwoRooms(listOfRooms.get(i), listOfRooms.get(i + 1), world);
                    connectedRooms.union(i, i + 1);
                }
                connectTwoRooms(listOfRooms.get(numberOfRooms - 1), listOfRooms.get(0), world);
                connectedRooms.union(numberOfRooms - 1, 0);
            }
        }
    }


    // Works
    private void drawBox(Position p, int w, int h, TETile[][] tiles, TETile type) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[p.x + i][p.y + j] = type;
                isFloor.add(new Position(p.x + i, p.y + j));
            }
        }
    }

    // Returns true if collision, false if no collision
    private boolean validate(Position p, int w, int h) {
        for (Map.Entry<Position, Position> entry : addedRooms.entrySet()) {
            if (helperValidate(p, w, h, entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return sizeValidate(p, w, h);
    }

    private boolean sizeValidate(Position toAdd, int w, int h) {
        if (toAdd.y == 0) {
            return true;
        }
        if (toAdd.x == 0) {
            return true;
        }
        if (toAdd.x + w >= WIDTH - 1) {
            return true;
        }
        if (toAdd.y + h >= HEIGHT - 1) {
            return true;
        }
        return false;
    }
    // Returns true if collision, false if no collision
    private boolean helperValidate(Position toAdd, int w, int h, Position addedStart,
                                   Position addedFinal) {
        if (toAdd.y <= addedFinal.y && (toAdd.y + h) >= addedStart.y) {
            if (toAdd.x <= addedFinal.x && (toAdd.x + w) >= addedStart.x) {
                return true;
            }
        }
        return false;
    }

    private Position randPosition() {
        int x = Math.abs(rand.nextInt() % WIDTH);
        int y = Math.abs(rand.nextInt() % HEIGHT);
//        int x = RandomUtils.uniform(rand, WIDTH - 4);
//        int y = RandomUtils.uniform(rand, HEIGHT - 4);
        return new Position(x, y);
    }

    // Works, wont create hallway if p1 and p2 are same point
    private void createHallway(Position p1, Position p2, TETile[][] world) {
        drawVerticalHall(p1, p2, world);
        drawHorizontalHall(p1, p2, world);
    }

    // Works
    private void drawVerticalHall(Position p1, Position p2, TETile[][] world) {
        int distY = Math.abs(p1.y - p2.y);
        if (p1.y == p2.y) {
            return;
        }
        if (p2.y > p1.y) {
            for (int i = 0; i <= distY; i++) {
                world[p1.x][p1.y + i] = Tileset.SAND;
                isFloor.add(new Position(p1.x, p1.y + i));
            }
        } else if (p2.y < p1.y) {
            for (int i = 0; i <= distY; i++) {
                world[p1.x][p1.y - i] = Tileset.SAND;
                isFloor.add(new Position(p1.x, p1.y - i));
            }
        }
    }

    // Works
    private void drawHorizontalHall(Position p1, Position p2, TETile[][] world) {
        int distX = Math.abs(p1.x - p2.x);
        if (p1.x == p2.x) {
            return;
        }
        if (p2.x > p1.x) {
            for (int i = 0; i <= distX; i++) {
                world[p1.x + i][p2.y] = Tileset.SAND;
                isFloor.add(new Position(p1.x + i, p2.y));
            }
        } else if (p2.x < p1.x) {
            for (int i = 0; i <= distX; i++) {
                world[p1.x - i][p2.y] = Tileset.SAND;
                isFloor.add(new Position(p1.x - i, p2.y));
            }
        }
    }

    // Works
    private void connectTwoRooms(Room r1, Room r2, TETile[][] world) {
        Position r1RandomPos = r1.getRandomInRange();
        Position r2RandomPos = r2.getRandomInRange();
        createHallway(r1RandomPos, r2RandomPos, world);
    }

    private void placeWalls(TETile[][] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (j != 0 && tiles[i][j] == Tileset.NOTHING && tiles[i][j - 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (j != tiles[0].length - 1 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i][j + 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (i != 0 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i - 1][j] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (i != tiles.length - 1 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i + 1][j] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (j != 0 && i != 0 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i - 1][j - 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (j != tiles[0].length - 1 && i != 0 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i - 1][j + 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (i != tiles.length - 1 && j != 0 && tiles[i][j] == Tileset.NOTHING
                        && tiles[i + 1][j - 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
                if (i != tiles.length - 1 && j != tiles[0].length - 1
                        && tiles[i][j] == Tileset.NOTHING
                        && tiles[i + 1][j + 1] == Tileset.SAND) {
                    tiles[i][j] = Tileset.WALL;
                }
            }
        }
    }
    private String valueOfCursor(TETile[][] cachedWorld) {
        int xPos = (int) StdDraw.mouseX() / 1;
        int yPos = (int) StdDraw.mouseY() / 1;
        if (cachedWorld[xPos][yPos] == Tileset.NOTHING) {
            return "Nothing";
        }
        else if (cachedWorld[xPos][yPos] == Tileset.WALL) {
            return "Wall";
        }
        else if (cachedWorld[xPos][yPos] == Tileset.SAND) {
            return "Ground";
        }
        else if (cachedWorld[xPos][yPos] == Tileset.FLOWER) {
            return aName;
        } else if (cachedWorld[xPos][yPos] == Tileset.GRASS) {
            return "Grass";
        } else {
            return "Mystery";
        }
    }

    private void limeLight(int radius, TETile[][] world) {
        Position p = aPos;
        Color black = new Color(0, 0, 0);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!(Math.abs(p.x - i) < radius) || !(Math.abs(p.y - j) < radius)) {
                    world[i][j] = new TETile('a', black, black, "black");
                }
            }
        }
    }


}
