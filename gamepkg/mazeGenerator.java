package gamepkg;
import java.util.Queue;
import java.util.LinkedList;

import java.util.Random;

import java.util.List;
import java.util.ArrayList;


public class mazeGenerator{
    private final int rows, cols;

    private double difficulty = 1.0;
    public int[] goal = new int[2];
    public int startX, startY;
    private final Entity[][] map;
    private Random rand = new Random();
    public mazeGenerator(int rows, int cols, int seed, double difficultyPercent){
        this.rand = new Random(seed);
        this.rows = rows;
        this.cols = cols;
        this.map = new Entity[rows][cols];
        this.difficulty = difficultyPercent;
        primsGenerator();
    }
    public mazeGenerator(int rows, int cols){
        this(rows, cols, 51421, 1.0);
    }
    public mazeGenerator(int rows, int cols, int seed){
        this(rows, cols, seed, 1.0);
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
        goal[0] = goalX;
        goal[1] = goalY;
        map[goalY][goalX] = new Goal(goalX, goalY);

    }
    private List<List<int[]>> splitList(int splitIndex, List<int[]> originalList){
        List<int[]> leftList = new ArrayList<>();
        List<int[]> rightList = new ArrayList<>();
        List<List<int[]>> combinedLists = new ArrayList<>();

        for(int i = 0; i < splitIndex; i++){
            leftList.add(originalList.get(i));
        }
        for(int i = splitIndex; i < originalList.size(); i++){
            rightList.add(originalList.get(i));
        }
        combinedLists.add(leftList);
        combinedLists.add(rightList);
        return combinedLists;
    }
    private List<int[]> merge(List<int[]> leftList, List<int[]> rightList){
        List<int[]> mergedList = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;
        //since were sorting based on distance and the arr is {x, y, distance}
        //array index is 2
        while(leftIndex < leftList.size() && rightIndex < rightList.size()){
            if(leftList.get(leftIndex)[2] < rightList.get(rightIndex)[2]){
                mergedList.add(leftList.get(leftIndex));
                leftIndex++;
            }
            else{
                mergedList.add(rightList.get(rightIndex));
                rightIndex++;
            }
        }
        while(leftIndex < leftList.size()){
            mergedList.add(leftList.get(leftIndex));
            leftIndex++;
        }
        while(rightIndex < rightList.size()){
            mergedList.add(rightList.get(rightIndex));
            rightIndex++;
        }
        return mergedList;
    }
    private List<int[]> mergeSortPossiblePaths(List<int[]> possiblePaths){
        if(possiblePaths.size() <= 1){
            return possiblePaths;
        }
        int middle = possiblePaths.size()/2;
        List<List<int[]>> combinedLists = splitList(middle, possiblePaths);

        List<int[]> leftList = combinedLists.get(0);
        List<int[]> rightList = combinedLists.get(1);
        leftList = mergeSortPossiblePaths(leftList);
        rightList = mergeSortPossiblePaths(rightList);

        return merge(leftList, rightList);

    }

    private List<int[]> sortPossiblePaths(List<int[]> possiblePaths){
        List<int[]> sortedPossiblePaths = mergeSortPossiblePaths(possiblePaths);
        return sortedPossiblePaths;


    }
    private int[] breadthFirstSeacrch(int startX, int startY){
        Queue<int[]> bfsQueue = new LinkedList<>();

        List<int[]> possiblePaths = new ArrayList<>();


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
                    possiblePaths.add(new int[]{newX, newY, newSize});
                    searched[newY][newX] = true;

                    if(furthestTile[2] < newSize){
                        furthestTile = newTile;
                    }
                    
                    bfsQueue.add(new int[]{newX, newY, newSize});
                }
                
            }
        }
        possiblePaths = sortPossiblePaths(possiblePaths);
        /*for(int i = 0; i < possiblePaths.size(); i++){
            for(int j = 0; j < possiblePaths.get(i).length; j++){
                System.out.print(possiblePaths.get(i)[j] + " ");
            }
            System.out.println();
        }*/
        double adjustedDistance = furthestTile[2] * difficulty;
        int[] startTile = possiblePaths.get(1);
        for(int i = 1; i < possiblePaths.size(); i++){
            if(possiblePaths.get(i)[2] >= adjustedDistance){
                startTile = possiblePaths.get(i - 1);
                break;
            }
        }
        System.out.println(possiblePaths.get(possiblePaths.size() -1)[2]);
        if(startTile[2] == possiblePaths.get(0)[2]){
            startTile = possiblePaths.get(1); // force minimum difficulty
        }

        return startTile;
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