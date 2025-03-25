package gamepkg;

class Effect {
    protected String effectType = "None";
    protected int start, end, step;

    protected boolean effectEnded = false;

    protected Player p;
    void setupEffect(){

    }
    void apply(){

    }
}
class Fire extends Effect{
    
    public Fire(int duration, Player player){
        end = duration;
        p = player;
        effectType = "Fire";
    }
    @Override
    public void apply(){
        if(step >= end){
            this.effectEnded = true;
        }
        else{
            p.health--;
            step++;
        }
    }
}
class Push extends Effect{
    private String direction;
    public Push(Player player, String dir){
        direction = dir;
        p = player;
    }
    @Override
    public void apply(){
        //catch case if the effectis done and not cleaned up
        if(effectEnded) return;
        int newIndex = -1;
        for(int i = 0; i < p.possibleDirections.length; i++){
            if(p.possibleDirections[i].equals(direction)){
                newIndex = i;
            }
        }
        //catch case if invalid direction
        if(newIndex == -1)return;
        
        p.movementIndex = newIndex;
        p.move();

        effectEnded = true;
    }
}

