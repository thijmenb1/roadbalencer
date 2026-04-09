import greenfoot.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Vehicle class that pathfinds using BFS (Breadth-First Search)
 * and moves along the calculated path.
 * 
 * @author (your name) 
 * @version 1.0
 */
public class Vehicle extends Actor
{
    private int current_row;
    private int current_col;
    private int target_row;
    private int target_col;
    private List<int[]> path;
    private int move_counter;
    private int move_speed;  // Number of frames before moving to next tile
    private static final int MAP_ROWS = 20;
    private static final int MAP_COLS = 20;
    private static final int TILE_SIZE = 32;
    
    /**
     * Constructor for Vehicle
     * @param start_row Starting row on the map
     * @param start_col Starting column on the map
     * @param target_row Target row on the map
     * @param target_col Target column on the map
     */
    public Vehicle(int start_row, int start_col, int target_row, int target_col) {
        this.current_row = start_row;
        this.current_col = start_col;
        this.target_row = target_row;
        this.target_col = target_col;
        this.path = null;
        this.move_counter = 0;
        this.move_speed = 10;  // Change this number to control speed (higher = slower)
    }
    
    /**
     * Constructor with custom speed
     * @param start_row Starting row on the map
     * @param start_col Starting column on the map
     * @param target_row Target row on the map
     * @param target_col Target column on the map
     * @param speed Number of frames before moving to next tile (higher = slower)
     */
    public Vehicle(int start_row, int start_col, int target_row, int target_col, int speed) {
        this.current_row = start_row;
        this.current_col = start_col;
        this.target_row = target_row;
        this.target_col = target_col;
        this.path = null;
        this.move_counter = 0;
        this.move_speed = speed;
    }
    
    /**
     * Called when the Vehicle is added to the world
     */
    public void addedToWorld(World world) {
        System.out.println("=== Vehicle Added to World ===");
        System.out.println("Start position: [" + current_row + "][" + current_col + "]");
        System.out.println("Target position: [" + target_row + "][" + target_col + "]");
        System.out.println("Move speed: " + move_speed + " frames per tile");
        
        // Set initial spawn position in the center of the starting tile
        setLocation(current_col * TILE_SIZE + TILE_SIZE / 2, current_row * TILE_SIZE + TILE_SIZE / 2);
        
        // Calculate path
        path = findPath(current_row, current_col, target_row, target_col);
        
        if (path != null) {
            System.out.println("Path found! Length: " + path.size());
        } else {
            System.out.println("ERROR: No path found!");
        }
    }
    
    /**
     * Act - moves the Vehicle along the calculated path
     */
    public void act() {
        move_counter++;
        
        if (move_counter >= move_speed) {
            move_counter = 0;
            
            if (path != null && !path.isEmpty()) {
                int[] next = path.get(0);
                int next_row = next[0];
                int next_col = next[1];
                
                // Move to the next tile
                setLocation(next_col * TILE_SIZE + TILE_SIZE / 2, next_row * TILE_SIZE + TILE_SIZE / 2);
                path.remove(0);
                
                System.out.println("Moving to: [" + next_row + "][" + next_col + "] - Remaining: " + path.size());
            } else if (path != null && path.isEmpty()) {
                System.out.println("Destination reached!");
            }
        }
    }
    
    /**
     * Finds a path from start to target using BFS
     * @return List of int[] coordinates representing the path, or null if no path exists
     */
    private List<int[]> findPath(int start_row, int start_col, int target_row, int target_col) {
        int[][] map = Level.map;
        
        System.out.println("Start tile value: " + map[start_row][start_col]);
        System.out.println("Target tile value: " + map[target_row][target_col]);
        
        // Validate start and target positions
        if (!isValidTile(start_row, start_col, map)) {
            System.out.println("ERROR: Start tile [" + start_row + "][" + start_col + "] is not walkable!");
            return null;
        }
        
        if (!isValidTile(target_row, target_col, map)) {
            System.out.println("ERROR: Target tile [" + target_row + "][" + target_col + "] is not walkable!");
            return null;
        }
        
        // Initialize BFS
        boolean[][] visited = new boolean[MAP_ROWS][MAP_COLS];
        int[][] came_from_row = new int[MAP_ROWS][MAP_COLS];
        int[][] came_from_col = new int[MAP_ROWS][MAP_COLS];
        
        // Initialize came_from arrays with -1 to detect unvisited nodes
        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLS; j++) {
                came_from_row[i][j] = -1;
                came_from_col[i][j] = -1;
            }
        }
        
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{start_row, start_col});
        visited[start_row][start_col] = true;
        came_from_row[start_row][start_col] = start_row;
        came_from_col[start_row][start_col] = start_col;
        
        // Direction vectors: up, down, left, right
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        
        // BFS
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];
            
            // Check if we reached the target
            if (r == target_row && c == target_col) {
                System.out.println("Target found! Tracing path...");
                return tracePath(came_from_row, came_from_col, start_row, start_col, target_row, target_col);
            }
            
            // Explore neighbors
            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                
                if (isValidTile(nr, nc, map) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    came_from_row[nr][nc] = r;
                    came_from_col[nr][nc] = c;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        
        System.out.println("ERROR: No path found between start and target!");
        return null;
    }
    
    /**
     * Checks if a tile is valid and walkable (non-zero values are walkable)
     */
    private boolean isValidTile(int row, int col, int[][] map) {
        return row >= 0 && row < MAP_ROWS && 
               col >= 0 && col < MAP_COLS && 
               map[row][col] != 0;
    }
    
    /**
     * Traces back the path from target to start using the came_from arrays
     */
    private List<int[]> tracePath(int[][] came_from_row, int[][] came_from_col, 
                                   int start_row, int start_col, int target_row, int target_col) {
        List<int[]> path = new ArrayList<>();
        int r = target_row;
        int c = target_col;
        int steps = 0;
        
        // Trace back from target to start
        while ((r != start_row || c != start_col) && steps < MAP_ROWS * MAP_COLS) {
            path.add(new int[]{r, c});
            
            int prev_r = came_from_row[r][c];
            int prev_c = came_from_col[r][c];
            
            if (prev_r == -1 || prev_c == -1) {
                System.out.println("ERROR: Path trace failed - unvisited node encountered!");
                return null;
            }
            
            r = prev_r;
            c = prev_c;
            steps++;
        }
        
        if (steps >= MAP_ROWS * MAP_COLS) {
            System.out.println("ERROR: Path trace exceeded maximum steps!");
            return null;
        }
        
        // Add the starting position
        path.add(new int[]{start_row, start_col});
        
        // Reverse to get path from start to target
        Collections.reverse(path);
        
        return path;
    }
}
