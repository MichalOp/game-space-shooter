package com.codingame.game;

public class Action implements Comparable<Action>{
    public enum ActionType {Missile, Move, Fire, Detonate, Wait}
    public int unitId;
    public ActionType type;
    public Vector2d direction;

    public Action(){
        unitId = -1;
        type = ActionType.Wait;
        direction = new Vector2d();
    }

    @Override
    public int compareTo(Action a) {
        return this.type.ordinal() - a.type.ordinal();
    }
}
