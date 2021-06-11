package com.codingame.game;

import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class SideBar  {
    Referee ref;
    int faction;
    int verticalLayout;
    Rectangle healthBar;
    Ship ship;
    SideBar(int faction, Referee ref, Ship ship){
        this.faction=faction;
        this.ship = ship;
        this.ref=ref;
        this.verticalLayout = faction == 1 ? 0 : Consts.MAP_Y/2;

    }

    void update(){

    }


}
