import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Level extends World
{
    // Display & Rendering
    private static final int TILE_SIZE = 32;
    private static final int WORLD_WIDTH = 1600;
    private static final int WORLD_HEIGHT = 768;
    private static final int UI_PANEL_X = 1550;
    private static final int UI_PANEL_Y = 650;

    public static GreenfootImage[] tiles;

    private int effected_col;
    private int effected_row;
    private boolean rKeyWasDown = false;

    // Animation
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 15;

    // Terrain Generation
    private static final int FOREST_SPAWN_CHANCE = 10;
    private static final int HOUSE_SPAWN_CHANCE = 3;
    private static final int RESOURCE_FACTORY_PAIRS = 1;

    // Direction vectors
    private static final int[][] ADJACENT_DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // TILE IDS - Terrain
    private static final int GRASS = 0;
    private static final int FOREST = 32;
    private static final int HOUSE = 33;

    // TILE IDS - Roads
    private static final int ROAD_V = 1;
    private static final int ROAD_H = 2;
    private static final int CROSS = 3;
    private static final int T_JUNCTION_UP = 4;
    private static final int T_JUNCTION_RIGHT = 5;
    private static final int T_JUNCTION_DOWN = 6;
    private static final int T_JUNCTION_LEFT = 7;
    private static final int CORNER_NE = 8;
    private static final int CORNER_SE = 9;
    private static final int CORNER_SW = 10;
    private static final int CORNER_NW = 11;

    // TILE IDS - Depots
    private static final int DEPOT_START = 12;
    private static final int DEPOT_END = 15;
    private static final int DEPOT_PICKUP_LEFT = 12;
    private static final int DEPOT_PICKUP_DOWN = 13;
    private static final int DEPOT_PICKUP_RIGHT = 14;
    private static final int DEPOT_PICKUP_UP = 15;

    // TILE IDS - Resources & Factories
    private static final int RESOURCE_1 = 16;
    private static final int RESOURCE_2 = 17;
    private static final int RESOURCE_3 = 18;
    private static final int FACTORY_1 = 20;
    private static final int FACTORY_2 = 21;
    private static final int FACTORY_3 = 22;

    // TILE IDS - Rivers
    private static final int RIVER_V = 24;
    private static final int RIVER_H = 25;
    private static final int RIVER_CROSSING_V = 26;
    private static final int RIVER_CROSSING_H = 27;
    private static final int RIVER_CORNER_NE = 28;
    private static final int RIVER_CORNER_NW = 29;
    private static final int RIVER_CORNER_SW = 30;
    private static final int RIVER_CORNER_SE = 31;

    // TILE IDS - Drills & Animation
    private static final int DRILL = 39;
    private static final int DRILL_RESOURCE_1 = 40;
    private static final int DRILL_RESOURCE_1_2 = 36;
    private static final int DRILL_RESOURCE_2 = 41;
    private static final int DRILL_RESOURCE_2_2 = 37;
    private static final int DRILL_RESOURCE_3 = 42;
    private static final int DRILL_RESOURCE_3_2 = 38;

    // Costs
    private static final int COST_GRASS = 1;
    private static final int COST_FOREST = 2;
    private static final int COST_HOUSE = 5;
    private static final int COST_RIVER_CROSSING = 3;
    private static final int COST_DRILL = 5;
    private static final int REFUND_FOREST = 1;
    private static final int REFUND_HOUSE = 4;

    // Quick select keys
    public static final int[] QUICK_SELECT_TILES = {
        ROAD_V,
        CORNER_NW,
        ROAD_H,
        T_JUNCTION_UP,
        DEPOT_PICKUP_RIGHT,
        DRILL
    };

    public static boolean game_started = false;

    public static int selected_tile = 1;

    public static int row_depot_pickup_1 = 5;
    public static int col_depot_pickup_1 = 5;

    public static int row_depot_dropof_1 = 18;
    public static int col_depot_dropof_1 = 18;

    private boolean firstDepotPlaced = false;
    private boolean secondDepotPlaced = false;
    private boolean vehicleSpawned = false;

    private boolean building = false;
    private boolean removing = false;

    public static int money = 999999;

    public static int[][] map = new int[24][50];

    public Level()
    {
        super(WORLD_WIDTH, WORLD_HEIGHT, 1);
        loadTiles();
        generateTerrain();
        generateRiver();
        spawnResourcesAndFactories();
        drawMap();
        addObject(new ui(), UI_PANEL_X, UI_PANEL_Y);
    }
    public void act()
    {
        selectTile();

        animationCounter++;

        drawMap();

        drawRoads();

        highlightTile();

        if (Greenfoot.isKeyDown("space"))
        {
            game_started = true;
        }
    }
    private void generateTerrain()
    {
        for (int row = 0; row < map.length; row++)
        {
            for (int col = 0; col < map[row].length; col++)
            {
                int random = Greenfoot.getRandomNumber(100);

                if (random < FOREST_SPAWN_CHANCE)
                {
                    map[row][col] = FOREST;
                }
                else if (random < FOREST_SPAWN_CHANCE + HOUSE_SPAWN_CHANCE)
                {
                    map[row][col] = HOUSE;
                }
                else
                {
                    map[row][col] = GRASS;
                }
            }
        }
    }
    private void generateRiver()
    {
        int row = Greenfoot.getRandomNumber(map.length - 6) + 3;
        int col = 0;

        while (col < map[0].length)
        {
            map[row][col] = RIVER_H;
            int turnChance = Greenfoot.getRandomNumber(100);

            // TURN UP
            if (turnChance < 20 && row > 2 && col < map[0].length - 1)
            {
                map[row][col] = RIVER_CORNER_NW;
                int verticalLength = 1 + Greenfoot.getRandomNumber(3);
                for (int i = 0; i < verticalLength && row > 0; i++)
                {
                    row--;
                    map[row][col] = RIVER_V;
                }
                if (col + 1 < map[0].length)
                {
                    map[row][col] = RIVER_CORNER_SE;
                }
            }
            // TURN DOWN
            else if (turnChance > 80 && row < map.length - 3 && col < map[0].length - 1)
            {
                map[row][col] = RIVER_CORNER_SW;
                int verticalLength = 1 + Greenfoot.getRandomNumber(3);
                for (int i = 0; i < verticalLength && row < map.length - 1; i++)
                {
                    row++;
                    map[row][col] = RIVER_V;
                }
                map[row][col] = RIVER_CORNER_NE;
            }

            col++;
        }
    }
    private void spawnResourcesAndFactories()
    {
        for (int i = 0; i < RESOURCE_FACTORY_PAIRS; i++)
        {
            int resourceType = 16 + Greenfoot.getRandomNumber(3);

            int factoryType = 20 + (resourceType - 16);

            placeTileRandomly(resourceType);

            placeTileRandomly(factoryType);
        }
    }
    private void placeTileRandomly(int tileType)
    {
        while (true)
        {
            int row = Greenfoot.getRandomNumber(map.length);

            int col = Greenfoot.getRandomNumber(map[0].length);

            if (map[row][col] == GRASS)
            {
                map[row][col] = tileType;

                return;
            }
        }
    }

    private void spawnVehicle()
    {
        int x = col_depot_pickup_1 * TILE_SIZE + TILE_SIZE / 2;
        int y = row_depot_pickup_1 * TILE_SIZE + TILE_SIZE / 2;
        
        String color = getDrillColor(row_depot_pickup_1, col_depot_pickup_1);
        
        addObject(
            new Vehicle(
                row_depot_pickup_1,
                col_depot_pickup_1,
                row_depot_dropof_1,
                col_depot_dropof_1,
                color,
                true
            ),
            x,
            y
        );
    }
    
    private String getDrillColor(int row, int col)
    {
        for (int[] dir : ADJACENT_DIRECTIONS)
        {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (isValidPosition(newRow, newCol))
            {
                int tile = map[newRow][newCol];
                
                if (tile == DRILL_RESOURCE_1 || tile == DRILL_RESOURCE_1_2 || tile == RESOURCE_1)
                {
                    return "red";
                }
                else if (tile == DRILL_RESOURCE_2 || tile == DRILL_RESOURCE_2_2 || tile == RESOURCE_2)
                {
                    return "blue";
                }
                else if (tile == DRILL_RESOURCE_3 || tile == DRILL_RESOURCE_3_2 || tile == RESOURCE_3)
                {
                    return "yellow";
                }
            }
        }
        
        return "red"; // Default to red if no adjacent drill found
    }
    private void loadTiles()
    {
        final int TILEMAP_COLS = 4;
        final int TOTAL_TILES = 44;
        GreenfootImage sheet = new GreenfootImage("tilemap_concept.png");
        tiles = new GreenfootImage[TOTAL_TILES];

        for (int i = 0; i < TOTAL_TILES; i++)
        {
            int col = i % TILEMAP_COLS;
            int row = i / TILEMAP_COLS;
            GreenfootImage tile = new GreenfootImage(TILE_SIZE, TILE_SIZE);
            tile.drawImage(sheet, -(col * TILE_SIZE), -(row * TILE_SIZE));
            tiles[i] = tile;
        }
    }
    private void drawMap()
    {
        GreenfootImage bg = getBackground();
        for (int row = 0; row < map.length; row++)
        {
            for (int col = 0; col < map[row].length; col++)
            {
                int tileId = getAnimatedTileId(map[row][col]);
                bg.drawImage(tiles[tileId], col * TILE_SIZE, row * TILE_SIZE);
            }
        }
    }

    private int getAnimatedTileId(int tileId)
    {
        int frame = (animationCounter / ANIMATION_SPEED) % 2;
        if (tileId == DRILL_RESOURCE_1 || tileId == DRILL_RESOURCE_1_2) {
            return frame == 0 ? DRILL_RESOURCE_1 : DRILL_RESOURCE_1_2;
        } else if (tileId == DRILL_RESOURCE_2 || tileId == DRILL_RESOURCE_2_2) {
            return frame == 0 ? DRILL_RESOURCE_2 : DRILL_RESOURCE_2_2;
        } else if (tileId == DRILL_RESOURCE_3 || tileId == DRILL_RESOURCE_3_2) {
            return frame == 0 ? DRILL_RESOURCE_3 : DRILL_RESOURCE_3_2;
        }
        return tileId;
    }
    private void drawRoads()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();

        if (mouse == null || game_started)
        {
            return;
        }

        effected_col = mouse.getX() / TILE_SIZE;

        effected_row = mouse.getY() / TILE_SIZE;

        if (effected_col < 0 || effected_col >= map[0].length || effected_row < 0 || effected_row >= map.length)
        {
            return;
        }

        if (Greenfoot.mousePressed(null))
        {
            if (mouse.getButton() == 1)
            {
                building = true;
            }

            if (mouse.getButton() == 3)
            {
                removing = true;
            }
        }

        if (Greenfoot.mouseDragged(null))
        {
            if (building)
            {
                handleLeftClick();
            }

            if (removing)
            {
                handleRightClick();
            }
        }

        if (Greenfoot.mouseClicked(null))
        {
            if (mouse.getButton() == 1)
            {
                handleLeftClick();
            }

            if (mouse.getButton() == 3)
            {
                handleRightClick();
            }

            building = false;

            removing = false;
        }
    }
    private void handleLeftClick()
    {
        if (!isValidPosition(effected_row, effected_col) || money == 0)
        {
            return;
        }

        int currentTile = map[effected_row][effected_col];
        if (currentTile == selected_tile)
        {
            return;
        }

        switch (currentTile)
        {
            case GRASS:
                if (selected_tile != DRILL)
                {
                    money -= COST_GRASS;
                    placeAndDepotCheck(COST_GRASS);
                }
                break;

            case FOREST:
                if (selected_tile != DRILL)
                {
                    money -= COST_FOREST;
                    placeAndDepotCheck(COST_FOREST);
                }
                break;

            case HOUSE:
                if (selected_tile != DRILL)
                {
                    money -= COST_HOUSE;
                    placeAndDepotCheck(COST_HOUSE);
                }
                break;

            case RIVER_V:
                if (selected_tile == ROAD_H)
                {
                    money -= COST_RIVER_CROSSING;
                    map[effected_row][effected_col] = RIVER_CROSSING_H;
                }
                break;

            case RIVER_H:
                if (selected_tile == ROAD_V)
                {
                    money -= COST_RIVER_CROSSING;
                    map[effected_row][effected_col] = RIVER_CROSSING_V;
                }
                break;

            case RESOURCE_1:
                if (selected_tile == DRILL)
                {
                    money -= COST_DRILL;
                    map[effected_row][effected_col] = DRILL_RESOURCE_1;
                    checkAndSetDepot(effected_row, effected_col);
                }
                break;

            case RESOURCE_2:
                if (selected_tile == DRILL)
                {
                    money -= COST_DRILL;
                    map[effected_row][effected_col] = DRILL_RESOURCE_2;
                    checkAndSetDepot(effected_row, effected_col);
                }
                break;
            
            case RESOURCE_3:
                if (selected_tile == DRILL)
                {
                    money -= COST_DRILL;
                    map[effected_row][effected_col] = DRILL_RESOURCE_3;
                    checkAndSetDepot(effected_row, effected_col);
                }
                break;
            
            case RIVER_CORNER_NE:
            case RIVER_CORNER_NW:
            case RIVER_CORNER_SW:
            case RIVER_CORNER_SE:
            case RIVER_CROSSING_V:
            case RIVER_CROSSING_H:
            case DRILL_RESOURCE_1:
            case DRILL_RESOURCE_2:
            case DRILL_RESOURCE_3:
            case FACTORY_1:
            case FACTORY_2:
            case FACTORY_3:
                break;

            default:
                map[effected_row][effected_col] = selected_tile;
                if (isDepotTile(selected_tile))
                {
                    checkAndSetDepot(effected_row, effected_col);
                }
        }
    }

    private void placeAndDepotCheck(int cost)
    {
        map[effected_row][effected_col] = selected_tile;
        if (isDepotTile(selected_tile))
        {
            checkAndSetDepot(effected_row, effected_col);
        }
    }

    private boolean isDepotTile(int tile)
    {
        return tile >= DEPOT_START && tile <= DEPOT_END;
    }

    private void checkAndSetDepot(int row, int col)
    {
        int tile = map[row][col];

        if (isDepotTile(tile))
        {
            boolean nextToDrill = isAdjacentTo(row, col, new int[]{DRILL, DRILL_RESOURCE_1, DRILL_RESOURCE_2, DRILL_RESOURCE_3});
            boolean nextToFactory = isAdjacentTo(row, col, new int[]{FACTORY_1, FACTORY_2, FACTORY_3});

            if (nextToDrill && !firstDepotPlaced)
            {
                row_depot_pickup_1 = row;
                col_depot_pickup_1 = col;
                firstDepotPlaced = true;
            }
            else if (nextToFactory && !secondDepotPlaced)
            {
                row_depot_dropof_1 = row;
                col_depot_dropof_1 = col;
                secondDepotPlaced = true;
            }
        }
        else if (isDrillTile(tile))
        {
            for (int[] dir : ADJACENT_DIRECTIONS)
            {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isValidPosition(newRow, newCol) && isDepotTile(map[newRow][newCol]))
                {
                    checkAndSetDepot(newRow, newCol);
                }
            }
            return;
        }

        if (firstDepotPlaced && secondDepotPlaced && !vehicleSpawned)
        {
            spawnVehicle();
            vehicleSpawned = true;
        }
    }

    private boolean isDrillTile(int tile)
    {
        return tile == DRILL || tile == DRILL_RESOURCE_1 || tile == DRILL_RESOURCE_2 || tile == DRILL_RESOURCE_3;
    }

    private boolean isAdjacentTo(int row, int col, int[] tileTypes)
    {
        for (int[] dir : ADJACENT_DIRECTIONS)
        {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidPosition(newRow, newCol))
            {
                int tile = map[newRow][newCol];
                for (int type : tileTypes)
                {
                    if (tile == type)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidPosition(int row, int col)
    {
        return row >= 0 && row < map.length && col >= 0 && col < map[0].length;
    }

    private void handleRightClick()
    {
        if (!isValidPosition(effected_row, effected_col))
        {
            return;
        }
        
        int currentTile = map[effected_row][effected_col];

        switch (currentTile)
        {
            case GRASS:
                break;
            
            case RIVER_CROSSING_V:
                money += COST_RIVER_CROSSING;
                map[effected_row][effected_col] = RIVER_H;
                break;
            
            case RIVER_CROSSING_H:
                money += COST_RIVER_CROSSING;
                map[effected_row][effected_col] = RIVER_V;
                break;
            
            case HOUSE:
                money -= REFUND_HOUSE;
                map[effected_row][effected_col] = GRASS;
                break;
            
            case FOREST:
                money -= REFUND_FOREST;
                map[effected_row][effected_col] = GRASS;
                break;
        
            case DRILL_RESOURCE_1:
                money += COST_DRILL;
                map[effected_row][effected_col] = RESOURCE_1;
                break;
            
            case DRILL_RESOURCE_2:
                money += COST_DRILL;
                map[effected_row][effected_col] = RESOURCE_2;
                break;
            
            case DRILL_RESOURCE_3:
                money += COST_DRILL;
                map[effected_row][effected_col] = RESOURCE_3;
                break;

            case RIVER_V:
            case RIVER_H:
            case RIVER_CORNER_NE:
            case RIVER_CORNER_NW:
            case RIVER_CORNER_SW:
            case RIVER_CORNER_SE:
            case RESOURCE_1:
            case RESOURCE_2:
            case RESOURCE_3:
            case FACTORY_1:
            case FACTORY_2:
            case FACTORY_3:
                break;
            
            default:
                if (isDepotTile(currentTile))
                {
                    firstDepotPlaced = false;
                    secondDepotPlaced = false;
                    vehicleSpawned = false;
                }
                money++;
                map[effected_row][effected_col] = GRASS;
        }
    }

    private void selectTile()
    {
        updateSelectedTile("1", ROAD_V);
        updateSelectedTile("2", CORNER_NW);
        updateSelectedTile("3", CROSS);
        updateSelectedTile("4", T_JUNCTION_UP);
        updateSelectedTile("5", DEPOT_PICKUP_RIGHT);
        updateSelectedTile("6", DRILL);

        if (Greenfoot.isKeyDown("r") && !rKeyWasDown)
        {
            rotate();
        }

        rKeyWasDown = Greenfoot.isKeyDown("r");
    }

    private void updateSelectedTile(String key, int tileId)
    {
        if (Greenfoot.isKeyDown(key))
        {
            selected_tile = tileId;
        }
    }

    private void highlightTile()
    {
        if (effected_row < 0 || effected_row >= map.length || effected_col < 0 || effected_col >= map[0].length)
        {
            return;
        }
        getBackground().setColor(new Color(0, 150, 255, 100));
        getBackground().fillRect(effected_col * TILE_SIZE, effected_row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private void rotate()
    {
        selected_tile = rotateThrough(
            selected_tile,
            new int[]{ROAD_V, ROAD_H},
            new int[]{T_JUNCTION_UP, T_JUNCTION_DOWN, T_JUNCTION_LEFT, T_JUNCTION_RIGHT},
            new int[]{CORNER_NW, CORNER_SW, CORNER_SE, CORNER_NE},
            new int[]{DEPOT_PICKUP_RIGHT, DEPOT_PICKUP_DOWN, DEPOT_PICKUP_LEFT, DEPOT_PICKUP_UP}
        );
    }

    private int rotateThrough(int current, int[]... cycles)
    {
        for (int[] cycle : cycles)
        {
            for (int i = 0; i < cycle.length; i++)
            {
                if (cycle[i] == current)
                {
                    return cycle[(i + 1) % cycle.length];
                }
            }
        }
        return current;
    }
}

