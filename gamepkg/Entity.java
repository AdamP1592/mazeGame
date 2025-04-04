package gamepkg;


import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

public class Entity{
    protected int x, y, width, height;
    
    public boolean passable, moveable = false;
    public Color color;
    public Entity(int x, int y, int width, int height, boolean passable){
        this.x = x; 
        this.y = y;
        this.width = width;
        this.height = height;

        this.passable = passable;
        this.color = new Color(213, 213, 213);
    }
    public void onStep(Player p){

    }
    public Effect getEffect(){
        
        return new Effect();
    }
}
class Player extends Entity{
    private int[][] moveArr = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    public String[] possibleDirections = {"n", "e", "s", "w"};

    public int movementIndex = 0;

    private int[] movement;

    private List<Effect> activeEffects = new ArrayList<>();

    public boolean isDead = false;
    
    int health = 10;

    int w = 2;
    int h = 1;

    public Player(int x, int y){
        super(x, y, 1, 1, true);
        this.moveable = true;
        this.color = new Color (73, 173, 162);
        movement = moveArr[movementIndex];
    

    }
    public void addEffect(Effect e){
        activeEffects.add(e);
    }
    public void turn(String dir){

        if (dir.equals("right")) {
            movementIndex = (movementIndex + 1) % moveArr.length;
        } else if (dir.equals("left")) {
            movementIndex = (movementIndex + moveArr.length - 1) % moveArr.length;
        }
        movement = moveArr[movementIndex];
        int tempHeight = height;

        height = width;
        width = tempHeight;
    }
    public int[] getMovement(){
        int[] movementVector = movement;
        //possibly apply some effect to the movement vector

        return movementVector;
    }
    public void move(int [] movementVector){
        if(moveable){
            this.x += movementVector[0];
            this.y += movementVector[1];
        }
    }
    public void iterateEffects(){
        for(Effect e:activeEffects){
            e.apply(this);
        }
        if(health < 0){
            isDead = true;
            color = new Color(168, 50, 68);
            System.out.println("Ded");
        }
        activeEffects.removeIf(e->e.effectEnded);
    }
}

class Wall extends Entity{
    public Wall(int x, int y, int width, int height){
        super(x, y, width, height, false);
        this.color = new Color(94, 94, 94);
    }
}
class Floor extends Entity{
    public Floor(int x, int y, int width, int height){
        super(x, y, width, height, true);
    }
    
}
class TrappedFloor extends Floor{
    private String[] effects = {"fire", "stick", "heal"};
    private String effectString;
    private int numUses;
    public TrappedFloor(int x, int y, String effect){
        super(x, y, 1, 1);
        effectString = effect;
        this.color = new Color(224, 159, 27);
        
        setUpEffectParams();
    }
    public void setUpEffectParams(){
        Effect[] effectHolder = {new Fire(3), new Stick(1), new Heal(2)};

        //iterates through the effects to find if the given effect is valid
        for(int i = 0; i < effects.length; i++){
            if(effects[i].equals(effectString)){
                //replaces the tile effect
                Effect e = effectHolder[i];
                this.numUses = e.numUses;
                this.color = e.color;
            }
        }
    }
    //returns the effect which can be called by e.apply(Player p)
    @Override
    public void onStep(Player p){
        if(numUses == 0) return;
        
        numUses--;
        p.addEffect(getEffect());
    }
    @Override
    public Effect getEffect(){
        Effect e = new Effect();

        //creates new holder for each effect type
        Effect[] effectHolder = {new Fire(3), new Stick(1), new Heal(2)};

        //iterates through the effects to find if the given effect is valid
        for(int i = 0; i < effects.length; i++){
            if(effects[i].equals(effectString)){
                //replaces the tile effect
                e = effectHolder[i];
                return e;
            }
        }
        //returns the default empty effect if there is no valid effect
        return e;
    }
}
class Goal extends Floor{
    public Goal(int x, int y){
        super(x, y, 1, 1);
        this.color = new Color(255, 255, 0);
        moveable = true;
    }
    public void playerEntered(Player p){
        
    }
}
