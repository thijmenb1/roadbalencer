import greenfoot.*;

/**
 * UI class - displays quick-select tile panel and game information
 */
public class ui extends Actor
{
    // UI Constants
    private static final int SLOT_SIZE = 32;
    private static final int SLOT_PADDING = 12;
    private static final int COLS = 2;
    private static final int ROWS = 3;
    private static final int ROW_SPACING = SLOT_SIZE + SLOT_PADDING + 10;
    private static final int PANEL_PADDING = 10;
    private static final int KEY_LABEL_SIZE = 12;
    private static final int KEY_LABEL_Y_OFFSET = 14;
    private static final int TEXT_SIZE = 12;
    private static final int TEXT_Y_OFFSET_TILE = 28;
    private static final int TEXT_Y_OFFSET_MONEY = 10;

    private String[] tileNames = {
        "grass",             // 0
        "road v",            // 1
        "road h",            // 2
        "cross",             // 3
        "t-junct up",        // 4
        "t-junct right",     // 5
        "t-junct down",      // 6
        "t-junct left",      // 7
        "corner ne",         // 8
        "corner se",         // 9
        "corner sw",         // 10
        "corner nw",         // 11
        "depot left",        // 12
        "depot down",        // 13
        "depot right",       // 14
        "depot up",          // 15
        "res blue",          // 16
        "res red",           // 17
        "res yellow",        // 18
        "unused",            // 19
        "fac blue",          // 20
        "fac red",           // 21
        "fac yellow",        // 22
        "unused",            // 23
        "river v",           // 24
        "river h",           // 25
        "river cross v",     // 26
        "river cross h",     // 27
        "river corner ne",   // 28
        "river corner nw",   // 29
        "river corner sw",   // 30
        "river corner se",   // 31
        "forest",            // 32
        "house",             // 33
        "unused",            // 34
        "unused",            // 35
        "drill res blue 2",  // 36
        "drill res red 2",   // 37
        "drill res yellow 2",// 38
        "drill",             // 39
        "drill res blue",    // 40
        "drill res red",     // 41
        "drill res yellow",  // 42
        "unused"             // 43
    };

    private static final int[] QUICK_SELECT_TILES = {1, 11, 3, 4, 14, 39};

    private static final int[][] QUICK_SELECT_GROUPS = {
        {1, 2},
        {11, 10, 9, 8},
        {3},
        {4, 5, 6, 7},
        {14, 13, 12, 15},
        {39}
    };
   
    public void act()
    {
        selectedTile();
    }
    public void selectedTile()
    {
        int gridWidth = COLS * SLOT_SIZE + (COLS - 1) * SLOT_PADDING;
        int gridHeight = SLOT_SIZE * ROWS + (ROWS - 1) * ROW_SPACING;
        int panelWidth = gridWidth + 2 * PANEL_PADDING;
        int panelHeight = gridHeight + 2 * PANEL_PADDING;

        GreenfootImage img = new GreenfootImage(panelWidth, panelHeight);
        img.setColor(new Color(0, 0, 0, 180));
        img.fillRect(0, 0, panelWidth, panelHeight);

        int startX = PANEL_PADDING;
        int startY = PANEL_PADDING;

        for (int i = 0; i < QUICK_SELECT_TILES.length; i++)
        {
            int displayTile = getDisplayTileIdForSlot(i);
            int col = i % COLS;
            int row = i / COLS;
            int x = startX + col * (SLOT_SIZE + SLOT_PADDING);
            int y = startY + row * ROW_SPACING;

            drawSlot(img, i, x, y, displayTile);
        }

        drawTextInfo(img, panelWidth, panelHeight);
        setImage(img);
    }

    private void drawSlot(GreenfootImage img, int slotIndex, int x, int y, int displayTile)
    {
        if (isSelectedQuickSlot(slotIndex))
        {
            img.setColor(Color.YELLOW);
            img.drawRect(x - 1, y - 1, SLOT_SIZE + 1, SLOT_SIZE + 1);
        }

        GreenfootImage slotTile = getTileImage(displayTile, SLOT_SIZE, SLOT_SIZE);
        img.drawImage(slotTile, x, y);

        img.setFont(new Font("Arial", false, false, KEY_LABEL_SIZE));
        img.setColor(Color.WHITE);
        String keyLabel = String.valueOf(slotIndex + 1);
        int keyWidth = keyLabel.length() * 7;
        int keyX = x + (SLOT_SIZE - keyWidth) / 2;
        img.drawString(keyLabel, keyX, y + SLOT_SIZE + KEY_LABEL_Y_OFFSET);
    }

    private void drawTextInfo(GreenfootImage img, int panelWidth, int panelHeight)
    {
        img.setFont(new Font("Arial", false, false, TEXT_SIZE));
        img.setColor(Color.WHITE);

        // Selected tile text
        String tileLabel = getTileLabel(Level.selected_tile);
        int textWidth = tileLabel.length() * 7;
        int textX = (panelWidth - textWidth) / 2;
        img.drawString(tileLabel, textX, panelHeight - TEXT_Y_OFFSET_TILE);

        // Money text
        String moneyText = "$" + Level.money;
        int moneyWidth = moneyText.length() * 7;
        int moneyX = (panelWidth - moneyWidth) / 2;
        img.drawString(moneyText, moneyX, panelHeight - TEXT_Y_OFFSET_MONEY);
    }

    private String getTileLabel(int tileId)
    {
        if (tileId >= 0 && tileId < tileNames.length)
        {
            return tileNames[tileId];
        }
        return "tile " + tileId;
    }

            private GreenfootImage getTileImage(int tileId, int width, int height)
            {
                GreenfootImage tile;
                if (tileId >= 0 && tileId < Level.tiles.length)
                {
                    tile = new GreenfootImage(Level.tiles[tileId]);
                }
                else
                {
                    tile = new GreenfootImage(width, height);
                    tile.setColor(Color.DARK_GRAY);
                    tile.fillRect(0, 0, width, height);
                }

                tile.scale(width, height);
                return tile;
            }

    private boolean isSelectedQuickSlot(int slotIndex)
    {
        int current = Level.selected_tile;
        for (int option : QUICK_SELECT_GROUPS[slotIndex])
        {
            if (option == current)
            {
                return true;
            }
        }
        return false;
    }

    private int getDisplayTileIdForSlot(int slotIndex)
    {
        int current = Level.selected_tile;
        for (int option : QUICK_SELECT_GROUPS[slotIndex])
        {
            if (option == current)
            {
                return current;
            }
        }
        return QUICK_SELECT_TILES[slotIndex];
    }
}
