package gamepkg;

import java.util.Arrays;

public class MazeGame {
    private int randomSeed = 512;
    private int width;
    private int height;
    public int[] goal = new int[2];

    public Player p;
    public Entity[][] maze;

    // Constructor
    public MazeGame(int width, int height, int seed, double difficultyPercent) {
        this.randomSeed = seed;
        this.width = width;
        this.height = height;
        maze = setupMaze(width, height, difficultyPercent);
    }
    public MazeGame(int width, int height, int seed){
        this(width, height, seed, 1.0);
    }

    public MazeGame(int width, int height) {
        this(width, height, 512, 1.0); // default seed
    }

    private Entity[][] setupMaze(int width, int height, double difficultyPercent) {
        mazeGenerator m = new mazeGenerator(height, width, randomSeed, difficultyPercent);
        Entity[][] map = m.getMaze();
        goal = m.goal;
        p = new Player(m.startX, m.startY);
        return map;
    }

    public double move(int movementOption) {
        // encoding: q = 0, w = 1, e = 2, s = 3

        double reward = 0.0;
        int backingUpModifier = (movementOption == 3) ? -1 : 1;

        if (movementOption == 0) p.turn("left");
        if (movementOption == 2) p.turn("right");

        int[] movementVector = Arrays.copyOf(p.getMovement(), 2);
        int newX = p.x + (movementVector[0] * backingUpModifier);
        int newY = p.y + (movementVector[1] * backingUpModifier);

        if (canMoveTo(newX, newY)) {
            if (maze[newY][newX].passable && p.moveable) {
                if ((movementOption == 1 || movementOption == 3) && p.moveable) {
                    if (movementOption == 3) {
                        movementVector[0] *= -1;
                        movementVector[1] *= -1;
                    }
                    p.move(movementVector);
                    maze[p.y][p.x].onStep(p);
                }
            }
        }
        else{
            return -0.3;//harsh punsihment for running into walls;
        }
        p.iterateEffects();
        return reward;
    }

    private boolean canMoveTo(int newX, int newY) {
        return newX >= 0 && newX < width &&
               newY >= 0 && newY < height &&
               maze[newY][newX].passable;
    }
    public double distanceToGoal(){
        double distance = 0.0;
        double xDistance = Math.pow(p.x - goal[0], 2);
        double yDistance = Math.pow(p.y - goal[1], 2);

        distance = Math.sqrt(xDistance + yDistance);

        return distance;
        
    }

    public boolean isGameOver() {
        return isDead() || goalReached();
    }
    
    public boolean isDead() {
        return p.health <= 0;
    }

    public boolean goalReached() {
        return maze[p.y][p.x] instanceof Goal;
    }
    
    public boolean canMove(){
        return p.moveable;
    }

    public int getHealth(){
        return p.health;
    }

    // get a flattened version of the maze (for RL input)
    public int[] getFlattenedMaze() {
        int[] flat = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                if (x == p.x && y == p.y) {
                    flat[index] = 3; // unique player code
                } else if (maze[y][x] instanceof Wall) {
                    flat[index] = 1;
                } else if (maze[y][x] instanceof Goal) {
                    flat[index] = 2;
                } else {
                    flat[index] = 0; // floor or other
                }
            }
        }
        return flat;
    }

    public int[] getPlayerPosition() {
        return new int[] { p.x, p.y };
    }
}
