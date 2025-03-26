package gamepkg;
import java.util.Queue;
import java.util.LinkedList;

import java.util.Random;

import java.util.List;
import java.util.ArrayList;


class mazeGenerator{
    private final int rows, cols;

    public int startX, startY;
    private final Entity[][] map;
    private Random rand = new Random();

    public mazeGenerator(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.map = new Entity[rows][cols];
        primsGenerator();
    }
    public mazeGenerator(int rows, int cols, int seed){
        this(rows, cols);
        this.rand = new Random(seed);
        primsGenerator();
    }
    public Entity[][] getMaze(){
        return map;
    }
    private void primsGenerator(){
        for (int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                map[y][x] = new Wall(x, y, 1, 1);
            }
        }
        //fire push health 
        double effectProbability = 0.1; 
        //ensure even starting point
        startX = rand.nextInt(cols/2) * 2;
        startY = rand.nextInt(rows/2) * 2;

        map[startY][startX] = new Floor(startX, startY, 1, 1);
        List<int[]> frontier = new ArrayList<>();
        addFrontier(startX, startY, frontier);

        while(!frontier.isEmpty()){
            int[] wall = frontier.remove(rand.nextInt(frontier.size()));
            int wallX = wall[0];
            int wallY = wall[1];
            String[] effectStrings = {"fire", "heal", "stick"};

            if(hasExactlyOneAdjacentFloor(wallX, wallY)){
                boolean hasEffect = rand.nextDouble() < effectProbability;
                addFrontier(wallX, wallY, frontier);
                if (hasEffect) {
                    int index = rand.nextInt(effectStrings.length);
                    map[wallY][wallX] = new TrappedFloor(wallX, wallY, effectStrings[index]);
                } else {
                    map[wallY][wallX] = new Floor(wallX, wallX, 1, 1);
                }
            }
            /* more standard prims
            for(int[] dir: directions){
                int newX = wallX + dir[0];
                int newY = wallY + dir[1];

                if(inBounds(newX, newY) && map[newY][newX] instanceof Wall){
                    int conX = wallX + dir[0]/2;
                    int conY = wallY + dir[1]/2;
                    
                    boolean hasEffect = rand.nextDouble() < effectProbability;
                    
                    map[conY][conX] = new Floor(conX, conY, 1, 1);
                    if(hasEffect){
                        int randomIndex = rand.nextInt(effectStrings.length);
                        map[newY][newX] = new TrappedFloor(newX, newY, effectStrings[randomIndex]);

                    }else{
                        map[newY][newX] = new Floor(newX, newY, 1, 1);
                    }
                }
            }*/

        }
        addGoal(startX, startY);
        
    }
    private void addGoal(int startX, int startY){
        int[] furthestTile = breadthFirstSeacrch(startX, startY);
        int goalX = furthestTile[0];
        int goalY = furthestTile[1];
        map[goalY][goalX] = new Goal(goalX, goalY);

    }
    private int[] breadthFirstSeacrch(int startX, int startY){
        Queue<int[]> bfsQueue = new LinkedList<>();
        bfsQueue.add(new int[]{startX, startY, 0});

        boolean[][] searched = new boolean[map.length][map[0].length];

        int[] furthestTile = new int[]{startX, startY, 0};

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        while(!bfsQueue.isEmpty()){
            //dequeue the current
            int[] currentPos = bfsQueue.poll();

            for(int i = 0; i < directions.length; i++){
                int newX = currentPos[0] + directions[i][0];
                int newY = currentPos[1] + directions[i][1];
                int newSize = currentPos[2] + 1;
                int[] newTile = new int[]{newX, newY, newSize};
                
                if(inBounds(newX, newY) &&
                    map[newY][newX].passable && 
                    !searched[newY][newX]){

                    searched[newY][newX] = true;

                    if(furthestTile[2] < newSize){
                        furthestTile = newTile;
                    }
                    
                    bfsQueue.add(new int[]{newX, newY, newSize});
                }
                
            }
        }
        return furthestTile;
    }
    private boolean hasExactlyOneAdjacentFloor(int x, int y) {
        int count = 0;
        for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (inBounds(nx, ny) && map[ny][nx] instanceof Floor) {
                count++;
            }
        }
        return count == 1;
    }
    private void addFrontier(int x, int y, List<int[]> frontier){
        for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
            int newX = x + d[0];
            int newY = y + d[1];
            if(inBounds(newX, newY) && map[newY][newX] instanceof Wall){
                frontier.add(new int[]{newX, newY});
            }
        }
    }
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

}