package com.codingame.game;

import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class Ship extends Unit {

    Vector2d acceleration;
    double gunCooldown;
    int missilesCount;
    Sprite graphics;
    Rectangle lifeBar;

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
                .setZIndex(1)
                .setX((int)position.x)
                .setY((int)position.y)
                .setRotation(Math.acos(startVelocity.x/startVelocity.length()));
        ref.tooltips.setTooltipText(graphics, toString());
        lifeBar= ref.graphicEntityModule.createRectangle()
                .setFillColor(faction==1 ? Consts.COLOR_1 : Consts.COLOR_0)
                .setY(graphics.getY())
                .setX(graphics.getX()-Consts.LIFE_BAR_WIDTH/2)
                .setZIndex(2)
                .setHeight(3)
                .setWidth(Consts.LIFE_BAR_WIDTH)
                .setScaleX(1);
    }

    @Override
    public String getUnitType() {
        return "S";
    }

    public int launchMissile(){
        if(missilesCount > 0){
            missilesCount--;
            return referee.addUnit(new Missile(position, velocity, faction, referee));
        }
        return -1;
    }

    public void setBurn(Vector2d direction){
        acceleration = direction.clip(1).mul(Consts.SHIP_MAX_ACCELERATION);
    }

    public void fire(Vector2d direction){
        if(gunCooldown > Consts.GUN_COOLDOWN) {
            Vector2d bulletVelocity = direction.clip(1).mul(Consts.BULLET_VELOCITY).add(velocity);
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
        graphics.setVisible(true);
        if((position.x<=50 && graphics.getX()>Consts.MAP_X-50) || (position.x>=Consts.MAP_X-50 && graphics.getX()<50) || (position.y>=Consts.MAP_Y-50 && graphics.getY()<50) || (position.y<=50 && graphics.getY()>Consts.MAP_Y-50) ){
            graphics.setVisible(false);
            referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        }
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        graphics.setRotation(Math.acos((position.x-graphics.getX())/(position.distance(new Vector2d(graphics.getX(), graphics.getY())))))
                .setX(((int)position.x))
                .setY(((int)position.y));
        System.out.println(graphics.getX() + " " + graphics.getY());
        lifeBar.setScaleX(health/Consts.SHIP_MAX_HEALTH)
                .setX(graphics.getX()-Consts.LIFE_BAR_WIDTH/2)
                .setY(graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, graphics);
        referee.graphicEntityModule.commitEntityState(t, lifeBar);
        referee.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String toString(){
        return "position: "+position.toString()+
                "\nacceleration: "+ acceleration.toString()+
                "\nvelocity: "+velocity.toString()+
                "\nhealth:"+ String.format("%.1f", health);

    }
}
