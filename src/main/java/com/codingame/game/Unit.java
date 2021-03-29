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
        position = position.add(velocity.mul(Consts.TIME_DELTA));
        position.x = (position.x + Math.ceil(1 + Math.abs(position.x / Consts.MAP_X))*Consts.MAP_X) % Consts.MAP_X;
        position.y = (position.y + Math.ceil(1 + Math.abs(position.y / Consts.MAP_Y))*Consts.MAP_Y) % Consts.MAP_Y;
    }

    public abstract String getUnitType();

    public void graphicsTick(double t){}

    public void tick(){}
}
