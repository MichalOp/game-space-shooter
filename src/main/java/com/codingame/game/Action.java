package com.codingame.game;

public class Action {
    public enum ActionType {Move, Fire, Detonate, Wait}
    public int unitId;
    public ActionType type;
    public Vector2d direction;

    public Action(){
        unitId = -1;
        type = ActionType.Wait;
        direction = new Vector2d();
    }
}
