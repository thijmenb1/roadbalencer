import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Vehicle class - represents a transport vehicle that moves between pickup and factory locations
 */
public class Vehicle extends Actor
{
    // Position tracking
    private int current_row;
    private int current_col;
    private int target_row;
    private int target_col;
    private int pickup_row;
    private int pickup_col;
    private int factory_row;
    private int factory_col;

    // Movement
    private int lastWaitLocation_row = -1;
    private int lastWaitLocation_col = -1;
    private int recalcCooldown = 0;
    private int targetX, targetY;
    private double posX, posY;
    private double speed = 64.0; // pixels per second
    private long lastActTime = 0;
    private boolean moving = false;
    private List<int[]> path;
    private int waitTimer = 0;

    // Constants
    private static final int WAIT_TIME = 120;  // 2 seconds at 60 FPS
    private static final int TILE_SIZE = 32;
    private static final int RECALC_COOLDOWN = 30;

    // Sprite variant selection
    private String color; // "red", "blue", "yellow"
    private boolean isFull;

    public Vehicle(int start_row, int start_col, int target_row, int target_col, String color, boolean isFull)
    {
        this.current_row = start_row;
        this.current_col = start_col;
        this.target_row = target_row;
        this.target_col = target_col;
        this.pickup_row = start_row;
        this.pickup_col = start_col;
        this.factory_row = target_row;
        this.factory_col = target_col;
        this.color = color;
        this.isFull = isFull;
    }

    public void act()
    {
        if (Level.game_started) {
            return;
        }
        
        // Handle waiting at destinations
        if (waitTimer > 0) {
            waitTimer--;
            if (waitTimer == 0) {
                handleDestinationReached();
            }
            return;
        }
        
        if (recalcCooldown > 0) recalcCooldown--;

        if ((path == null || path.isEmpty()) && recalcCooldown == 0) {
            path = findPath(current_row, current_col, target_row, target_col);
            recalcCooldown = RECALC_COOLDOWN;
            return;
        }

        // Get next tile if not moving
        if (!moving && path != null && !path.isEmpty()) {
            moveToNextTile();
        }

        // Smooth movement
        if (moving) {
            updateMovement();
        }
    }

    private void handleDestinationReached()
    {
        if (current_row == pickup_row && current_col == pickup_col) {
            // At pickup: fill up and head to factory
            isFull = true;
            setImageVariant();
            target_row = factory_row;
            target_col = factory_col;
            path = findPath(current_row, current_col, target_row, target_col);
            recalcCooldown = 0;
        } else if (current_row == factory_row && current_col == factory_col) {
            // At factory: empty and head back to pickup
            isFull = false;
            setImageVariant();
            target_row = pickup_row;
            target_col = pickup_col;
            path = findPath(current_row, current_col, target_row, target_col);
            recalcCooldown = 0;
        }
    }

    private void moveToNextTile()
    {
        int[] next = path.remove(0);
        current_row = next[0];
        current_col = next[1];
        targetX = current_col * TILE_SIZE + TILE_SIZE / 2;
        targetY = current_row * TILE_SIZE + TILE_SIZE / 2;
        moving = true;
    }

    private void updateMovement()
    {
        if (lastActTime == 0) {
            lastActTime = System.nanoTime();
        }
        long now = System.nanoTime();
        double deltaSeconds = (now - lastActTime) / 1_000_000_000.0;
        lastActTime = now;
        if (deltaSeconds <= 0) {
            return;
        }

        double dx = targetX - posX;
        double dy = targetY - posY;
        double distance = speed * deltaSeconds;

        // Calculate angle (with offset because sprite faces UP)
        int targetAngle = (int)Math.toDegrees(Math.atan2(dy, dx)) + 90;
        updateRotation(targetAngle);

        double distanceToTarget = Math.hypot(dx, dy);
        if (distanceToTarget <= distance) {
            posX = targetX;
            posY = targetY;
            setLocation(targetX, targetY);
            moving = false;
            checkDestinationArrival();
        }
        else {
            double moveX = Math.cos(Math.atan2(dy, dx)) * distance;
            double moveY = Math.sin(Math.atan2(dy, dx)) * distance;
            posX += moveX;
            posY += moveY;
            setLocation((int)Math.round(posX), (int)Math.round(posY));
        }
    }

    private void updateRotation(int targetAngle)
    {
        int current = getRotation();
        int diff = targetAngle - current;

        if (diff > 180) diff -= 360;
        if (diff < -180) diff += 360;

        int turnSpeed = 5;

        if (Math.abs(diff) < turnSpeed) {
            setRotation(targetAngle);
        } else {
            setRotation(current + (diff > 0 ? turnSpeed : -turnSpeed));
        }
    }

    private void checkDestinationArrival()
    {
        boolean atPickup = (current_row == pickup_row && current_col == pickup_col);
        boolean atFactory = (current_row == factory_row && current_col == factory_col);
        boolean justWaitedHere = (current_row == lastWaitLocation_row && current_col == lastWaitLocation_col);
        
        if ((atPickup || atFactory) && !justWaitedHere) {
            waitTimer = WAIT_TIME;
            lastWaitLocation_row = current_row;
            lastWaitLocation_col = current_col;
        }
    }
    
    public void addedToWorld(World world) {
        posX = current_col * TILE_SIZE + TILE_SIZE / 2;
        posY = current_row * TILE_SIZE + TILE_SIZE / 2;
        setLocation((int)Math.round(posX), (int)Math.round(posY));
        setImageVariant();
        path = findPath(current_row, current_col, target_row, target_col);
    }
    
    private void setImageVariant() {
        GreenfootImage tilemap = new GreenfootImage("trucks_topdown_spritesheet.png");
        int variantIndex = getVariantIndex();
        
        // Calculate row and column in the tilemap (2 rows, 3 columns)
        final int SPRITE_WIDTH = 11;
        final int SPRITE_HEIGHT = 32;
        int row = variantIndex / 3;
        int col = variantIndex % 3;
        int x = col * SPRITE_WIDTH;
        int y = row * SPRITE_HEIGHT;
        
        // Create a new image for this vehicle with the correct sprite
        GreenfootImage vehicleImage = new GreenfootImage(SPRITE_WIDTH, SPRITE_HEIGHT);
        vehicleImage.drawImage(tilemap, -x, -y);
        setImage(vehicleImage);
    }

    private int getVariantIndex()
    {
        if ("red".equalsIgnoreCase(color)) {
            return isFull ? 3 : 0;
        } else if ("blue".equalsIgnoreCase(color)) {
            return isFull ? 4 : 1;
        } else if ("yellow".equalsIgnoreCase(color)) {
            return isFull ? 5 : 2;
        }
        return 0;
    }
    
    public void changeVariant(String newColor, boolean newIsFull) {
        this.color = newColor;
        this.isFull = newIsFull;
        setImageVariant();
    }
    
    private List<int[]> findPath(int start_row, int start_col, int target_row, int target_col){
        int[][] map = Level.map;
        int numRows = map.length;
        int numCols = map[0].length;
        boolean[][] visited = new boolean[numRows][numCols];
        int[][] came_from_row = new int[numRows][numCols];
        int[][] came_from_col = new int[numRows][numCols];
        
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{start_row, start_col});
        visited[start_row][start_col] = true;
        
        int[] dr = {-1, 1, 0, 0};  // up, down, left, right
        int[] dc = {0, 0, -1, 1};
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];
            
            if (r == target_row && c == target_col) {
                return tracePath(came_from_row, came_from_col, start_row, start_col, target_row, target_col);
            }
            
            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (nr >= 0 && nr < numRows && nc >= 0 && nc < numCols && !visited[nr][nc]) {
                    int currentTile = map[r][c];
                    int nextTile = map[nr][nc];

                    if (nextTile != 0 &&
                        canMove(currentTile, dr[i], dc[i]) &&
                        canMove(nextTile, -dr[i], -dc[i])) {

                        visited[nr][nc] = true;
                        came_from_row[nr][nc] = r;
                        came_from_col[nr][nc] = c;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
        }
        return null; // no path found
    }
    private List<int[]> tracePath(int[][] came_from_row, int[][] came_from_col, int start_row, int start_col, int target_row, int target_col){
        List<int[]> path = new ArrayList<>();
        int r = target_row;
        int c = target_col;
        while (r != start_row || c != start_col) {

            path.add(new int[]{r, c});
            int prev_r = came_from_row[r][c];
            int prev_c = came_from_col[r][c];
            r = prev_r;
            c = prev_c;
        }
        path.add(new int[]{start_row, start_col});
        Collections.reverse(path);
        return path;
    }
    private boolean canMove(int tile, int dr, int dc) {
        // dr = change in row, dc = change in col
    
        //roads
        if (tile == 1) return (dr == -1 || dr == 1);
        if (tile == 2) return (dc == -1 || dc == 1);
    
        // Crossroad
        if (tile == 3) return true;

        // river crossings (bridges)
        if (tile == 26) return (dr == -1 || dr == 1); // RIVER_CROSSING_V
        if (tile == 27) return (dc == -1 || dc == 1); // RIVER_CROSSING_H
    
        // T junctions
        if (tile == 4) return (dr == -1 || dr == 1 || dc == 1); // right
        if (tile == 5) return (dc == -1 || dc == 1 || dr == -1); // up
        if (tile == 6) return (dc == -1 || dc == 1 || dr == 1); // down
        if (tile == 7) return (dr == -1 || dr == 1 || dc == -1); // left
    
        // Corners
        if (tile == 8) return (dr == -1 || dc == 1);  // NE
        if (tile == 9) return (dr == -1 || dc == -1); // NW
        if (tile == 10) return (dr == 1 || dc == -1); // SW
        if (tile == 11) return (dr == 1 || dc == 1);  // SE
    
        // Depots
        if (tile >= 12 && tile <= 15) return true;
    
        return false;
    }
}

