package com.codingame.game;

import com.codingame.gameengine.module.entities.Sprite;

public class Missile extends Unit{

    Vector2d acceleration;
    Sprite graphics;
    Sprite fire;

    public Missile(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref) {
        super(startPosition, startVelocity, faction, ref);
        health = Consts.MISSILE_MAX_HEALTH;
        acceleration = Vector2d.zero;
        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Missile_BLUE.png" : "Missile_GREEN.png")
                .setScale(0.1)
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
    }

    @Override
    public String getUnitType() {
        return "M";
    }

    public void setBurn(Vector2d direction){
        acceleration = direction.clip(1).mul(Consts.MISSILE_MAX_ACCELERATION);
    }

    @Override
    public void tick(){
        velocity = velocity.add(acceleration.mul(Consts.TIME_DELTA));
    }

    public void detonate(){
        health = 0;
    }

    @Override
    public void onDeath(double t){
        referee.registerExplosion(position, Consts.MISSILE_BLAST_RADIUS, Consts.MISSILE_DAMAGE);

        graphics.setVisible(false);
        fire.setX(graphics.getX()).setY(graphics.getY());
        fire.setScale(0.1);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, fire);
        fire.setVisible(true);
        fire.setScale(10);
        referee.graphicEntityModule.commitEntityState(t, fire);
        fire.setVisible(false);
    }

    @Override
    public void graphicsTick(double t){
        graphics.setVisible(true); //TODO transition instead of invisible
        if((position.x<=50 && graphics.getX()>Consts.MAP_X-50) || (position.x>=Consts.MAP_X-50 && graphics.getX()<50) || (position.y>=Consts.MAP_Y-50 && graphics.getY()<50) || (position.y<=50 && graphics.getY()>Consts.MAP_Y-50) ){
            graphics.setVisible(false);
            referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        }//TODO are these lines still useful? (missile, bullet, ship)

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
        return "position: "+position.toString()+
                "\nacceleration: "+ acceleration.toString()+
                "\nvelocity: "+velocity.toString()+
                "\nhealth:"+ String.format("%.1f", health);

    }
}
