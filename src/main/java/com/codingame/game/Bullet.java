package com.codingame.game;

import com.codingame.gameengine.module.entities.Sprite;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Bullet extends Unit{

    private double closestEnemy;

    double lifetime;
    Sprite graphics;
    Sprite fire;

    public Bullet(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        super(startPosition, startVelocity, faction, ref);
        health = Consts.BULLET_MAX_HEALTH;
        closestEnemy = Double.POSITIVE_INFINITY;
        lifetime = Consts.BULLET_LIFETIME;

        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Bullet_RED.png" : "Bullet_GREEN.png")
                .setScale(0.2)
                .setAnchor(0.5)
                .setX((int)position.x)
                .setY((int)position.y)
                .setRotation(Math.acos(startVelocity.x/startVelocity.length()));

        ref.tooltips.setTooltipText(graphics, toString());

        fire = ref.graphicEntityModule.createSprite()
                .setImage("FireBullet.png")
                .setScale(0)
                .setAnchor(0.5)
                .setX(graphics.getX())
                .setY(graphics.getY());
        ref.graphicEntityModule.commitEntityState(0, fire);
        debug_graphics.setRadius(4);
        debug_blast.setRadius((int)Consts.GUN_BLAST_RADIUS);
        referee.toggleModule.displayOnToggleState(fire, "debugToggle", false);
        referee.toggleModule.displayOnToggleState(graphics, "debugToggle", false);
    }

    @Override
    public String getUnitType() {
        return "B";
    }

    @Override
    public void tick(){
        if(lifetime <= 0){
            detonate();
        }
        lifetime -= Consts.TIME_DELTA;

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
                detonate();
            }
        }
    }

    void detonate(){
        health = 0;
    }

    @Override
    public void onDeath(double t){
        super.onDeath(t);
        referee.registerExplosion(position, Consts.GUN_BLAST_RADIUS, Consts.GUN_DAMAGE);

        graphics.setVisible(false);
        fire.setX(graphics.getX()).setY(graphics.getY());
        fire.setScale(0.1);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, fire);
        fire.setVisible(true);
        fire.setScale(5);
        referee.graphicEntityModule.commitEntityState(t, fire);
        fire.setVisible(false);

        debug_blast.setX(graphics.getX()).setY(graphics.getY());
        debug_blast.setVisible(true);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, debug_blast);
        debug_blast.setVisible(false);
    }

    @Override
    public void graphicsTick(double t){
        super.graphicsTick(t);
        graphics.setVisible(true);
        if((position.x<=50 && graphics.getX()>Consts.MAP_X-50) || (position.x>=Consts.MAP_X-50 && graphics.getX()<50) || (position.y>=Consts.MAP_Y-50 && graphics.getY()<50) || (position.y<=50 && graphics.getY()>Consts.MAP_Y-50) ){
            graphics.setVisible(false);
            referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        }
        graphics.setX(((int)position.x)%1920).setY(((int)position.y)%1080);

//        System.out.println(graphics.getX() + " " + graphics.getY());
        // referee.graphicEntityModule.commitEntityState(t, graphics);
        referee.tooltips.setTooltipText(graphics, toString());
    }
    @Override
    public String toString(){
        return "position: "+position.toString()+
                "\nvelocity: "+velocity.toString()+
                "\nlifetime: "+String.format("%.1f", lifetime);
    }
}




