package gamepkg;

import java.awt.Color;
public class Effect {
    protected String effectType = "None";
    protected int start, end, step;
    public int numUses = -1;

    public Color color = new Color(100, 100, 100);

    protected boolean effectEnded = false;

    void setupEffect(){

    }
    void apply(Player p){

    }
}
class Fire extends Effect{
    
    public Fire(int duration){
        end = duration;
        effectType = "Fire";
        color = new Color (224, 159, 27);
    }
    @Override
    public void apply(Player p){
        if(step >= end){
            this.effectEnded = true;
        }
        else{
            p.health--;
            step++;
        }
    }
}
class Stick extends Effect{
    
    public Stick(int duration){
        this.end = duration;
        effectType = "stick";
        color = new Color(132, 27, 224);
    }
    @Override
    public void apply(Player p){
        if(step >= end){
            p.moveable = true;
            this.effectEnded = true;
        }
        else{
            p.moveable = false;
            step++;
        }
    }
}
class Heal extends Effect{
    private int health;
    
    public Heal(int healAmount){ 
        health = healAmount;
        effectType = "heal";
        numUses = 2;
        color = new Color(224, 27, 27);
    }
    @Override
    public void apply(Player p){
        //catch case if the effectis done and not cleaned up
        if(effectEnded) return;
        p.health += health;
        
        //catch case if invalid direction
        effectEnded = true;
    }
}
