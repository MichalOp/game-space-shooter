package com.codingame.game;

import com.codingame.gameengine.module.entities.Sprite;

public class Ship extends Unit {

    Vector2d acceleration;
    double gunCooldown;
    int missilesCount;
    Sprite graphics;

    public Ship(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        super(startPosition, startVelocity, faction, ref);
        health = Consts.SHIP_MAX_HEALTH;
        gunCooldown = Consts.GUN_COOLDOWN;
        acceleration = Vector2d.zero;
        missilesCount = Consts.MISSILES_COUNT;
        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Spaceship_BLUE.png" : "Spaceship_GREEN.png")
                .setScale(0.2)
                .setAnchor(0.5)
                .setX((int)position.x)
                .setY((int)position.y)
                .setRotation(Math.acos(startVelocity.x/startVelocity.length()));
        ref.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String getUnitType() {
        return "S";
    }

    public void launchMissile(){
        if(missilesCount > 0){
            missilesCount--;
            referee.addUnit(new Missile(position, velocity, faction, referee));
        }
    }

    public void setBurn(Vector2d direction){
        acceleration = direction.clip(Consts.SHIP_MAX_ACCELERATION);
    }

    public void fire(Vector2d direction){
        if(gunCooldown > Consts.GUN_COOLDOWN) {
            Vector2d bulletVelocity = direction.mul(Consts.BULLET_VELOCITY).add(velocity);
            referee.addUnit(new Bullet(position, bulletVelocity, faction, referee));
            gunCooldown = 0;
        }
    }

    @Override
    public void tick(){
        velocity = velocity.add(acceleration.mul(Consts.TIME_DELTA));
        gunCooldown += Consts.TIME_DELTA;
    }

    @Override
    public void graphicsTick(double t){
        graphics.setVisible(true); //TODO transition instead of invisible
        if((position.x<=50 && graphics.getX()>Consts.MAP_X-50) || (position.x>=Consts.MAP_X-50 && graphics.getX()<50) || (position.y>=Consts.MAP_Y-50 && graphics.getY()<50) || (position.y<=50 && graphics.getY()>Consts.MAP_Y-50) ){
            graphics.setVisible(false);
            referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        }
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        graphics.setRotation(Math.acos((position.x-graphics.getX())/(position.distance(new Vector2d(graphics.getX(), graphics.getY())))))
                .setX(((int)position.x))
                .setY(((int)position.y));
        System.out.println(graphics.getX() + " " + graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, graphics);
        referee.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String toString(){
        return position.toString()+"\nhealth:"+ String.format("%.1f", health);

    }
}
