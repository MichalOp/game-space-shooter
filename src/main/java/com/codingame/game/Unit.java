package com.codingame.game;

public abstract class Unit {
    public Vector2d position;
    public Vector2d velocity;
    public int faction;
    public double health;
    public int id;
    //from what I understand this is not how you are supposed to write in Java (public variables should be avoided)
    protected Referee referee;

    public Unit(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        referee = ref;
        position = startPosition;
        velocity = startVelocity;
        this.faction = faction;
        this.id = ref.getId();
    }

    public void move(){
        Vector2d new_position = position.add(velocity.mul(Consts.TIME_DELTA));
        if (new_position.x < 0 || new_position.x > Consts.MAP_X || new_position.y < 0 || new_position.y > Consts.MAP_Y) {
            health = 0;
        } else {
            position = new_position;
        }
    }

    public void onDeath(double t){};

    public abstract String getUnitType();

    public abstract void graphicsTick(double t);

    public void tick(){}
}
