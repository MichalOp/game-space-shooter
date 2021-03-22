package com.codingame.game;

import com.codingame.gameengine.module.entities.Circle;

public class Ship extends Unit {

    Vector2d acceleration;
    double gunCooldown;
    Circle graphics;

    public Ship(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        super(startPosition, startVelocity, faction, ref, UnitType.Ship);
        System.out.println(position.x);
        health = Consts.SHIP_MAX_HEALTH;
        gunCooldown = Consts.GUN_COOLDOWN;
        acceleration = Vector2d.zero;
        graphics = ref.graphicEntityModule.createCircle()
                .setRadius(10)
                .setFillColor(0xffffff)
                .setX((int)position.x)
                .setY((int)position.y);
    }

    public void setBurn(Vector2d direction){
        acceleration = direction.clip(Consts.SHIP_MAX_ACCELERATION);
//        System.out.println(acceleration.x);
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
//        System.out.println(acceleration.x + " " + acceleration.y + " " + velocity.x + " " + velocity.y);
        gunCooldown += Consts.TIME_DELTA;
    }

    @Override
    public void graphicsTick(double t){
        graphics.setX(((int)position.x)).setY(((int)position.y));
        System.out.println(graphics.getX() + " " + graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, graphics);
    }
}
