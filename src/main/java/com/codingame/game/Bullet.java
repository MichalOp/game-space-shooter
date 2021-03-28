package com.codingame.game;

import com.codingame.gameengine.module.entities.Circle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Bullet extends Unit{

    private double closestEnemy;

    Circle graphics;

    public Bullet(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        super(startPosition, startVelocity, faction, ref);
        health = Consts.BULLET_MAX_HEALTH;
        closestEnemy = Double.POSITIVE_INFINITY;
        graphics = ref.graphicEntityModule.createCircle()
                .setRadius(3)
                .setFillColor(faction==1 ? Consts.COLOR_1 : Consts.COLOR_0)
                .setX((int)position.x)
                .setY((int)position.y);
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
        health = 0;
    }

    @Override
    public void onDeath(double t){
        for(Unit u : referee.getUnits()){
            double distance = u.position.distance(position);
            if(distance <= Consts.GUN_BLAST_RADIUS){
                u.health -= Consts.GUN_DAMAGE * (Consts.GUN_BLAST_RADIUS - distance) / Consts.GUN_BLAST_RADIUS;
            }
        }
        //TODO: Nicer graphics
        graphics.setRadius(3);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA/10, graphics);

        graphics.setVisible(false);
        graphics.setRadius((int)Consts.GUN_BLAST_RADIUS);
        referee.graphicEntityModule.commitEntityState(t, graphics);
    }

    @Override
    public void graphicsTick(double t){
        graphics.setX(((int)position.x)%1920).setY(((int)position.y)%1080);
        System.out.println(graphics.getX() + " " + graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, graphics);
    }
}
