package com.codingame.game;

import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;

public class Ship extends Unit {

    Vector2d acceleration;
    double gunCooldown;
    int missilesCount;
    Sprite graphics;
    int verticalLayout;
    Rectangle healthBar;
    Text healthText;
    Text missilesLeft;
    Text positionText;


    public Ship(Vector2d startPosition, Vector2d startVelocity, int faction, Referee ref, String nickName) {
        super(startPosition, startVelocity, faction, ref);
        verticalLayout = faction == 0 ? 0 : Consts.MAP_Y / 2;
        health = Consts.SHIP_MAX_HEALTH;
        gunCooldown = Consts.GUN_COOLDOWN;
        acceleration = Vector2d.zero;
        missilesCount = Consts.MISSILES_COUNT;
        graphics = ref.graphicEntityModule.createSprite()
                .setImage(faction == 1 ? "Spaceship_RED.png" : "Spaceship_GREEN.png")
                .setScale(0.2)
                .setAnchor(0.5)
                .setZIndex(1)
                .setX((int) position.x)
                .setY((int) position.y)
                .setRotation(Math.acos(startVelocity.x / startVelocity.length()));
        ref.tooltips.setTooltipText(graphics, toString());
        referee.graphicEntityModule.createText(nickName)
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(25)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY(verticalLayout)
                .setAnchorX(0.5);

        healthText = referee.graphicEntityModule.createText("Health: " + Consts.SHIP_MAX_HEALTH + "/10")
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(15)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY(verticalLayout + 30)
                .setAnchorX(0.5);
        healthBar = referee.graphicEntityModule.createRectangle()
                .setHeight(5)
                .setWidth(200)
                .setFillColor(faction == 1 ? Consts.COLOR_0 : Consts.COLOR_1)
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2 - 100)
                .setY(verticalLayout + 60)
                .setScaleX(1);
        positionText= referee.graphicEntityModule.createText("position (x, y): "+position.toString())
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(14)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY(verticalLayout + 75)
                .setAnchorX(0.5);
        missilesLeft = referee.graphicEntityModule.createText(missilesCount + " missiles left")
                .setStrokeThickness(5) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(20)
                .setFillColor(0x000000) // Setting the text color to black
                .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                .setY(verticalLayout + 100)
                .setAnchorX(0.5);


    }
    void drawSideBar(){
        healthText.setText("Health: "+String.format("%.1f", health)+"/10");
        healthBar.setScaleX(health/10);
        missilesLeft.setText(missilesCount+" missiles left");
        positionText.setText("position (x, y): "+position.toString());
    }

    @Override
    public String getUnitType() {
        return "S";
    }

    public int launchMissile(){
        if(missilesCount > 0){
            missilesCount--;
            return referee.addUnit(new Missile(position, velocity, faction, referee, Consts.MISSILES_COUNT- missilesCount-1));
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
        /*graphics.setVisible(true);
        if((position.x<=50 && graphics.getX()>Consts.MAP_X-50) || (position.x>=Consts.MAP_X-50 && graphics.getX()<50) || (position.y>=Consts.MAP_Y-50 && graphics.getY()<50) || (position.y<=50 && graphics.getY()>Consts.MAP_Y-50) ){
            graphics.setVisible(false);
            referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);
        }
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, graphics);*/
        graphics.setRotation(Math.atan2(graphics.getX()-position.x, position.y-graphics.getY())+Math.PI/2)
                .setX(((int)position.x))
                .setY(((int)position.y));
        System.out.println(graphics.getX() + " " + graphics.getY());

        drawSideBar();
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, healthBar);
        referee.graphicEntityModule.commitEntityState(t-Consts.TIME_DELTA, healthText);
        referee.graphicEntityModule.commitEntityState(t, missilesLeft);
        referee.graphicEntityModule.commitEntityState(t, positionText);
        referee.graphicEntityModule.commitEntityState(t, graphics);
        referee.tooltips.setTooltipText(graphics, toString());
    }

    @Override
    public String toString(){
        return  "\nacceleration: "+ acceleration.toString()+
                "\nvelocity: "+velocity.toString();

    }
}
