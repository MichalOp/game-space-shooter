package com.codingame.game;

import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;

public class Missile extends Unit{

    Vector2d acceleration;
    Sprite graphics;
    Sprite fire;
    int number;
    Text text;
    Rectangle healthBar;

    public Missile(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref, int number) {
        super(startPosition, startVelocity, faction, ref);
        this.number=number;
        health = Consts.MISSILE_MAX_HEALTH;
        acceleration = Vector2d.zero;
        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction==1 ? "Missile_RED.png" : "Missile_GREEN.png")
                .setScale(0.1)
                .setAnchor(0.5)
                .setX(0)
                .setY(0)
                .setRotation(0);

        graphics_group.add(graphics);
        ref.tooltips.setTooltipText(graphics, toString());
        fire = ref.graphicEntityModule.createSprite()
                .setImage("FireBullet.png")
                .setScale(0)
                .setAnchor(0.5)
                .setX(graphics.getX())
                .setY(graphics.getY());
        ref.graphicEntityModule.commitEntityState(0, fire);
        debug_graphics.setRadius(8);
        debug_graphics.setLineWidth(1.5).setLineColor(0x33a0ff);
        debug_blast.setRadius((int)Consts.MISSILE_BLAST_RADIUS);
        referee.toggleModule.displayOnToggleState(fire, "debugToggle", false);
        referee.toggleModule.displayOnToggleState(graphics, "debugToggle", false);
        text = referee.graphicEntityModule.createText("Missile id: "+id)
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(15)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY((faction == 0 ? 0 : Consts.MAP_Y / 2) + 130 + 30 * number)
                .setAnchorX(0.5);
        healthBar = referee.graphicEntityModule.createRectangle().setFillColor(faction == 1 ? Consts.COLOR_0 : Consts.COLOR_1)
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2 - 100)
                .setY((faction == 0 ? 0 : Consts.MAP_Y / 2) + 130 + 30 * number+20)
                .setHeight(3).setWidth(200);
    }

    @Override
    public String getUnitType() {
        return "M";
    }

    public void setBurn(Vector2d direction){
        acceleration = direction.clip(Consts.MISSILE_MAX_ACCELERATION);
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
        super.onDeath(t);
        referee.registerExplosion(position, Consts.MISSILE_BLAST_RADIUS, Consts.MISSILE_DAMAGE);
        text.setVisible(false);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, text);
        healthBar.setVisible(false);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, healthBar);
        graphics.setVisible(false);
        fire.setX(move_group.getX()).setY(move_group.getY());
        fire.setScale(0.1);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, fire);
        fire.setVisible(true);
        fire.setScale(10);
        referee.graphicEntityModule.commitEntityState(t, fire);
        fire.setVisible(false);

        debug_blast.setX(move_group.getX()).setY(move_group.getY());
        debug_blast.setVisible(true);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA/2, debug_blast);
        debug_blast.setVisible(false);
    }

    @Override
    public void graphicsTick(double t){
        super.graphicsTick(t);
        healthBar.setScaleX(health/Consts.MISSILE_MAX_HEALTH);
        referee.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String toString(){
        return "Missile, id "+this.id+
                "\nposition: "+position.toString()+
                "\nvelocity: "+velocity.toString()+
                "\nacceleration: "+ acceleration.toString()+
                "\nhealth: "+health;

    }
}
