package gamepkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
class Entity{
    protected int x, y, width, height;

    protected boolean passable, moveable = false;
    public Entity(int x, int y, int width, int height, boolean passable){
        this.x = x; 
        this.y = y;
        this.width = width;
        this.height = height;

        this.passable = passable;
    }
    
}
class Player extends Entity{
    private int[][] moveArr = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    public String[] possibleDirections = {"n", "e", "s", "w"};

    private Map<String, Effect> effectMap = new HashMap<>();
    public int movementIndex = 0;

    private int[] movement;

    private Effect[] activeEffects;
    
    int health = 10;

    int w = 2;
    int h = 1;

    public Player(int x, int y){
        super(x, y, 2, 1, true);
        this.moveable = true;
        movement = moveArr[movementIndex];

        effectMap.put("fire", new Fire(2, this));

    }
    public void addEffect(int effectType){
        
        
    }
    public void turn(String dir){

        if (dir.equals("right")) {
            movementIndex = (movementIndex + 1) % moveArr.length;
        } else if (dir.equals("left")) {
            movementIndex = (movementIndex + moveArr.length - 1) % moveArr.length;
        }
        movement = moveArr[movementIndex];
    }
    public void move(){
        int tempX, tempY;
        int[] dir = movement;
        tempX = dir[0];
        tempY = dir[1];

        this.x += tempX;
        this.y += tempY;
        

    }
}

class Wall extends Entity{
    
    public Wall(int x, int y, int width, int height){
        super(x, y, width, height, false);
    }
}
class Floor extends Entity{
    public Floor(int x, int y, int width, int height){
        super(x, y, width, height, true);
    }
}
class Goal extends Floor{
    public Goal(int x, int y){
        super(x, y, 1, 1);
    }
    public void playerEntered(Player p){
        
    }
}
