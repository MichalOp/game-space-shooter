package com.codingame.game;


import com.codingame.gameengine.module.entities.Sprite;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Bullet extends Unit{

    private double closestEnemy;

    Sprite graphics;
    Sprite fire;

    public Bullet(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        super(startPosition, startVelocity, faction, ref);
        health = Consts.BULLET_MAX_HEALTH;
        closestEnemy = Double.POSITIVE_INFINITY;

        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Bullet_BLUE.png" : "Bullet_GREEN.png")
                .setScale(0.2)
                .setAnchor(0.5)
                .setX((int)position.x)
                .setY((int)position.y)
                .setRotation(Math.acos(startVelocity.x/startVelocity.length()));

        fire = ref.graphicEntityModule.createSprite()
                .setImage("FireBullet.png")
                .setScale(0)
                .setX(graphics.getX())
                .setY(graphics.getY());
        ref.graphicEntityModule.commitEntityState(0, fire);
    }

    @Override
    public String getUnitType() {
        return "B";
    }

    @Override
    public void tick(){
        List<Unit> units = referee.getUnits();
        Optional<Unit> closest = units.stream().filter(x -> x.faction != faction).min(
                Comparator.comparingDouble(x -> position.distance(x.position)));

        double dist = Double.POSITIVE_INFINITY;
        if(closest.isPresent()){
            dist = position.distance(closest.get().position);
        }
        if (dist < closestEnemy){
            closestEnemy = dist;
        }else{
            if(closestEnemy < Consts.GUN_BLAST_RADIUS){
                Detonate();
            }
        }
    }

    void Detonate(){
        // TODO: Damage, graphics
        fire.setX(graphics.getX()).setY(graphics.getY()).setScale(0);//graphics, not tested
        referee.graphicEntityModule.commitEntityState(0.1, fire);

        health = 0;
    }

    @Override
    public void graphicsTick(double t){
        graphics.setX(((int)position.x)%1920).setY(((int)position.y)%1080);

        if(fire.getScaleX()==0.1){//detonation,  should bullet be deconstructed after detonation?
            fire.setScale(1);
            referee.graphicEntityModule.commitEntityState(0.9, fire);
        }
        if (fire.getScaleX()==1){
            fire.setScale(0);
            referee.graphicEntityModule.commitEntityState(1, fire);
        }

        System.out.println(graphics.getX() + " " + graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, graphics);
    }
}
