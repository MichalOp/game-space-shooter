package com.codingame.game;

import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;

public class Missile extends Unit{

    Vector2d acceleration;
    Sprite graphics;
    Sprite fire;
    int number;
    Text text;

    public Missile(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref, int number) {
        super(startPosition, startVelocity, faction, ref);
        this.number=number;
        health = Consts.MISSILE_MAX_HEALTH;
        acceleration = Vector2d.zero;
        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Missile_RED.png" : "Missile_GREEN.png")
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
        text = referee.graphicEntityModule.createText("Missile id: "+id)
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(15)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY((faction == 0 ? 0 : Consts.MAP_Y / 2) + 130 + 30 * number)
                .setAnchorX(0.5);
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
        text.setVisible(false);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, text);
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
        graphics.setRotation(Math.acos((position.x-graphics.getX())/(position.distance(new Vector2d(graphics.getX(), graphics.getY())))))
                .setX(((int)position.x))
                .setY(((int)position.y));
        System.out.println(graphics.getX() + " " + graphics.getY());
        referee.graphicEntityModule.commitEntityState(t, text);
        referee.graphicEntityModule.commitEntityState(t, graphics);
        referee.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String toString(){
        return "position: "+position.toString()+
                "\nacceleration: "+ acceleration.toString()+
                "\nvelocity: "+velocity.toString()+
                "\nid"+this.id;

    }
}
