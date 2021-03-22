package com.codingame.game;

public abstract class Unit {
    public Vector2d position;
    public Vector2d velocity;
    public int faction;
    public double health;
    public int id;
    public enum UnitType {Ship, Bullet, Missile}
    public UnitType type;
    //from what I understand this is not how you are supposed to write in Java (public variables should be avoided)
    protected Referee referee;

    public Unit(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref, UnitType type){
        referee = ref;
        position = startPosition;
        velocity = startVelocity;
        this.faction = faction;
        this.id = ref.getId();
        this.type = type;
    }

    public void move(){
        position = position.add(velocity.mul(Consts.TIME_DELTA));
        position.x = position.x % Consts.MAP_X;
        position.y = position.y % Consts.MAP_Y;
    }

    public String getUnitType() {
        switch (type) {
            case Ship:
                return "S";
            case Bullet:
                return "B";
            case Missile:
                return "M";
        }
        return "";
    }

    public void graphicsTick(double t){}

    public void tick(){}
}
