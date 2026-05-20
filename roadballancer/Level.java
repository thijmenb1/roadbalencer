import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Level extends World
{
    private static final int Tile_Size = 32;
    public static GreenfootImage[] tiles;
    private int effected_col;
    private int effected_row;
    private boolean rKeyWasDown = false;
    
    private static final int GRASS = 0;
    private static final int ROAD_V = 1;
    private static final int ROAD_H = 2;
    private static final int FOREST = 32;
    private static final int HOUSE = 33;
    private static final int RIVER_V = 24;
    private static final int RIVER_H = 25;
    private static final int RIVER_CROSSING_V = 26;
    private static final int RIVER_CROSSING_H = 27;
    private static final int RIVER_CORNER_NE = 28;
    private static final int RIVER_CORNER_NW = 29;
    private static final int RIVER_CORNER_SW = 30;
    private static final int RIVER_CORNER_SE = 31;
    
    public static boolean game_started = false;
    public static int selected_tile = 1;
    public static int row_depot_pickup_1 = 5;
    public static int col_depot_pickup_1 = 5;
    public static int row_depot_dropof_1 = 18;
    public static int col_depot_dropof_1 = 18;
    public static int money = 100;
    
    public static int[][] map = {
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 25, 25, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 0},
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    /*   
     0  = grass
     1  = road_v
     2  = road_h
     3  = crossroad
     4  = t_junction (right)
     5  = t_junction (up)
     6  = t_junction (down)
     7  = t_junction (left)
     8  = corner_NE
     9  = corner_NW
     10 = corner_SW
     11 = corner_SE
     12 = depot (left)
     13 = depot (down)
     14 = depot (right)
     15 = depot (up)
     16 = resource_b
     17 = resource_r
     18 = resource_y 
     19 = -
     20 = factory_b
     21 = factory_r
     22 = factory_y
     23 = sellpoint
     24 = river_v
     25 = river_h
     26 = river_crosing_v
     27 = river_crosing_h
     28 = river_corner_NE
     29 = river_corner_NW
     30 = river_corner_SW
     31 = river_corner_SE
     32 = forest
     33 = house
     34 = -
     35 = -
    */
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
     public Level()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(640, 640, 1);
        loadTiles();
        drawMap();
        addObject(new ui(), 56, 76);
        addObject(new Vehicle( col_depot_pickup_1, row_depot_pickup_1, col_depot_dropof_1, row_depot_dropof_1), col_depot_pickup_1 * 32-16, row_depot_pickup_1 * 32-16);
    }
    public void act(){
        selectTile();
        drawMap();
        drawRoads();
        highlightTile();
        if (Greenfoot.isKeyDown("space")){
            game_started = true;
        }
    }
    private void loadTiles(){
        GreenfootImage sheet = new GreenfootImage("tilemap_concept.png");
        tiles = new GreenfootImage[36];
        
        for (int i = 0; i < 36; i++){
            int col = i % 4;
            int row = i / 4;
        
            GreenfootImage tile = new GreenfootImage(Tile_Size, Tile_Size);
            tile.drawImage(sheet, -(col * Tile_Size), -(row * Tile_Size));
    
            tiles[i] = tile;
        }
    }
    private void drawMap() {
        GreenfootImage bg = getBackground();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                bg.drawImage(tiles[map[row][col]], col * Tile_Size, row * Tile_Size);
            }
        }
    }
    private void drawRoads() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse == null) {
            return;
        }

        effected_col = mouse.getX() / Tile_Size;
        effected_row = mouse.getY() / Tile_Size;

        if (!Greenfoot.mouseClicked(null) || game_started) {
            return;
        }

        if (mouse.getButton() == 1) {
            handleLeftClick();
        } else if (mouse.getButton() == 3) {
            handleRightClick();
        }
    }

    private void handleLeftClick() {
        if (money == 0) {
            return;
        }

        int currentTile = map[effected_row][effected_col];

        switch (currentTile) {
            case GRASS:
                money--;
                map[effected_row][effected_col] = selected_tile;
                break;
            case FOREST:
                money -= 2;
                map[effected_row][effected_col] = selected_tile;
                break;
            case HOUSE:
                money -= 5;
                map[effected_row][effected_col] = selected_tile;
                break;
            case RIVER_CORNER_NE:
            case RIVER_CORNER_NW:
            case RIVER_CORNER_SW:
            case RIVER_CORNER_SE:
                System.out.println("cant build on river corners");
                break;
            case RIVER_V:
                if (selected_tile == ROAD_H) {
                    money -= 3;
                    map[effected_row][effected_col] = RIVER_CROSSING_H;
                } else {
                    System.out.println("cannot build non-straight bridge on river");
                }
                break;
            case RIVER_H:
                if (selected_tile == ROAD_V) {
                    money -= 3;
                    map[effected_row][effected_col] = RIVER_CROSSING_V;
                } else {
                    System.out.println("cannot build non-straight bridge on river");
                }
                break;
            case RIVER_CROSSING_V:
            case RIVER_CROSSING_H:
                System.out.println("already a bridge here");
                break;
            default:
                map[effected_row][effected_col] = selected_tile;
        }
    }

    private void handleRightClick() {
        int currentTile = map[effected_row][effected_col];

        if (currentTile == RIVER_CROSSING_V) {
            money += 3;
            map[effected_row][effected_col] = RIVER_H;
            return;
        }

        if (currentTile == RIVER_CROSSING_H) {
            money += 3;
            map[effected_row][effected_col] = RIVER_V;
            return;
        }

        if (currentTile == GRASS
                || currentTile == RIVER_V
                || currentTile == RIVER_H
                || currentTile == RIVER_CORNER_NE
                || currentTile == RIVER_CORNER_NW
                || currentTile == RIVER_CORNER_SW
                || currentTile == RIVER_CORNER_SE) {
            System.out.println("cannot remove rivers");
            return;
        }

        money++;
        map[effected_row][effected_col] = GRASS;
    }
    private void selectTile() {
        if (Greenfoot.isKeyDown("1")) { selected_tile = 1; }    //straight
        if (Greenfoot.isKeyDown("2")) { selected_tile = 11; }   //corner
        if (Greenfoot.isKeyDown("3")) { selected_tile = 3; }    //cross
        if (Greenfoot.isKeyDown("4")) { selected_tile = 4; }    //t_junction
        if (Greenfoot.isKeyDown("5")) { selected_tile = 14; }    //depot
        if (Greenfoot.isKeyDown("r") && !rKeyWasDown) { rotate(); } rKeyWasDown = Greenfoot.isKeyDown("r");
    }
    private void highlightTile(){
        getBackground().setColor(new Color(0, 150, 255, 100));
        getBackground().fillRect(effected_col*32,effected_row*32,32,32);
    }
    private void rotate(){
        //roads
        if (selected_tile == 1){selected_tile = 2;}
        else if (selected_tile == 2){selected_tile = 1;}
        //t_junction
        else if (selected_tile == 4){selected_tile = 6;}
        else if (selected_tile == 6){selected_tile = 7;}
        else if (selected_tile == 7){selected_tile = 5;}
        else if (selected_tile == 5){selected_tile = 4;}
        //corners
        else if (selected_tile == 11){selected_tile = 10;}
        else if (selected_tile == 10){selected_tile = 9; }
        else if (selected_tile == 9 ){selected_tile = 8; }
        else if (selected_tile == 8 ){selected_tile = 11;}
        //depots
        else if (selected_tile == 14){selected_tile = 13;}
        else if (selected_tile == 13){selected_tile = 12;}
        else if (selected_tile == 12){selected_tile = 15;}
        else if (selected_tile == 15){selected_tile = 14;}
    }
}
