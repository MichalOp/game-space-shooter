package com.codingame.game;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;

import java.awt.*;

public abstract class Unit {
    public Vector2d position;
    public Vector2d velocity;
    public int faction;
    public double health;
    public int id;
    //from what I understand this is not how you are supposed to write in Java (public variables should be avoided)
    protected Referee referee;
    Sprite velocity_arrow;
    protected Circle debug_graphics;
    protected Circle debug_blast;
    protected Text debug_id;
    protected Group move_group;
    protected Group graphics_group;

    public Unit(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref){
        referee = ref;
        position = startPosition;
        velocity = startVelocity;
        this.faction = faction;
        this.id = ref.getId();
        velocity_arrow = ref.graphicEntityModule.createSprite().setImage("arrow.png")
                .setScale(0.01)
                .setAnchorY(0.5).setX(0).setY(0);

        debug_graphics = ref.graphicEntityModule.createCircle().setX(0).setY(0);
        debug_graphics.setFillColor( faction == 1 ? 0xff0000 : 0x00ff00);

        debug_blast = ref.graphicEntityModule.createCircle().setX((int)startPosition.x).setY((int)startPosition.y);
        debug_blast.setAlpha(0.4);
        debug_blast.setVisible(false);

        debug_id = ref.graphicEntityModule.createText()
                .setX(0)
                .setY(0)
                .setText("" + id)
                .setFillColor(0xffffff);

        referee.toggleModule.displayOnToggleState(debug_id, "debugToggle", true);

        referee.toggleModule.displayOnToggleState(debug_blast, "debugToggle", true);
        referee.toggleModule.displayOnToggleState(velocity_arrow, "debugToggle", true);
        referee.toggleModule.displayOnToggleState(debug_graphics, "debugToggle", true);

        graphics_group = ref.graphicEntityModule.createGroup(debug_graphics, velocity_arrow)
                .setX(0)
                .setY(0);

        move_group = ref.graphicEntityModule.createGroup(graphics_group, debug_id)
                .setX((int)position.x)
                .setY((int)position.y);
    }

    public void move(){
        Vector2d new_position = position.add(velocity.mul(Consts.TIME_DELTA));
        if (new_position.x < 0 || new_position.x > Consts.MAP_X || new_position.y < 0 || new_position.y > Consts.MAP_Y) {
            health = 0;
        } else {
            position = new_position;
        }
    }

    public void onDeath(double t){
        move_group.setVisible(false);
    }

    public abstract String getUnitType();

    public void graphicsTick(double t){
        move_group.setX((int)position.x).setY((int)position.y);
        if(velocity.x!=0 || velocity.y !=0) {
            graphics_group.setRotation(velocity.angle()-Math.PI/2);
        }
        velocity_arrow.setScaleX(velocity.length()/5000);
        referee.tooltips.setTooltipText(debug_graphics, toString());
    }

    public void tick(){}
}
