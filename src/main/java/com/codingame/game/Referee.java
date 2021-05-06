package com.codingame.game;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.view.AnimatedEventModule;
import com.codingame.view.ViewerEvent;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    private static int WIDTH = 1920;
    private static int HEIGHT = 1080;

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject public GraphicEntityModule graphicEntityModule;
    @Inject private AnimatedEventModule animatedEventModule;
    @Inject public TooltipModule tooltips;
    @Inject public EndScreenModule endScreenModule;


    private int unitId = 0;
    private List<Unit> unitList;

    public final List<Unit> getUnits(){
        return unitList;
    }

    public void addUnit(Unit u){
        unitList.add(u);
    }

    public int getId(){
        return unitId++;
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private void sendPlayerInputs() {

        // input for players:
        // number of lines
        // unitId, faction, type, health, posX, posY, velX, velY (ints for id & faction, rest as doubles with precision 2)
        // at least for now

        for (Player p : gameManager.getActivePlayers()) {
            p.sendInputLine(String.valueOf(getUnits().size()));
            for (Unit u : getUnits()) {
                p.sendInputLine(String.format("%d %d %s %.2f %.2f %.2f %.2f %.2f",
                        u.id, u.faction == p.getIndex() ? 1 : -1, u.getUnitType(), u.health, u.position.x, u.position.y, u.velocity.x, u.velocity.y));
            }
            p.execute();
        }
    }

    public void registerExplosion(Vector2d position, double radius, double damage){
        for(Unit u : unitList){
            double distance = u.position.distance(position);
            if(distance <= radius){
                u.health -= damage * (radius - distance) / radius;
            }
        }
    }

    void updateUnits(double t){
        List<Unit> dead = unitList.stream().filter(x -> x.health <= 0).collect(Collectors.toList());
        unitList = unitList.stream().filter(x -> x.health > 0).collect(Collectors.toList());
        while(!dead.isEmpty()){
            for(Unit u:dead){
                u.onDeath(t);
            }
            dead = unitList.stream().filter(x -> x.health <= 0).collect(Collectors.toList());
            unitList = unitList.stream().filter(x -> x.health > 0).collect(Collectors.toList());
        }
    }

    void doTurn(){
        double t = 0;
        graphicEntityModule.commitWorldState(0);
        while(t < 1 - 0.000001){
            t += Consts.TIME_DELTA;
            for (Unit u : unitList){
                u.move();
            }
            for (Unit u : unitList){
                u.graphicsTick(t);
            }
            for (Unit u : unitList){
                u.tick();
            }
            updateUnits(t);

        }
    }

    @Override
    public void init() {
        unitList = new ArrayList<>();

        gameManager.setFrameDuration(300);
        
        graphicEntityModule.createSprite().setImage("Background.jpg").setAnchor(0);

        for (Player p : gameManager.getPlayers()) {
            int faction = p.getIndex();
            Ship s = new Ship(new Vector2d(faction == 0 ? WIDTH/4 : WIDTH/4*3,HEIGHT/2), Vector2d.zero, faction, this);

            addUnit(s);
            p.ship = s;
            p.expectedOutputLines += 1;
        }
    }

    @Override
    public void gameTurn(int turn) {
        // Send new inputs with the updated positions
        sendPlayerInputs();
        System.out.println(String.format("here -- turn number: %d", turn));

        // Update new positions
        for (Player p : gameManager.getActivePlayers()) {
            try {
                List<Action> actions = p.getAction(unitList.stream()
                        .filter(x -> x.faction == p.getIndex())
                        .collect(Collectors.toList()));

                List<Action> shipActions = actions.stream()
                        .filter(x -> x.unitId == p.ship.id)
                        .collect(Collectors.toList());

                for (Action action : shipActions) {
                    System.out.println(String.format("%s", action.type.toString()));
                    if (action.type == Action.ActionType.Move) {
                        p.ship.setBurn(action.direction);
                    } else {
                        p.ship.fire(action.direction);
                    }
                }

            } catch (TimeoutException | NoSuchMethodException | InputMismatchException | NumberFormatException e) {
                p.deactivate(String.format("%s eliminated! Reason: %s", p.getNicknameToken(), e));
            }
        }

        doTurn();

        for (Player p : gameManager.getActivePlayers()) {
            if(p.ship.health <= 0){
                p.ship.health=0;
                tooltips.setTooltipText(p.ship.graphics, p.ship.toString());
                p.deactivate();
            }
        }

        if (gameManager.getActivePlayers().size() < 2 || turn > 200) {
            gameManager.endGame();
        }
    }

    @Override
    public void onEnd() {
        int[] scores = {0, 0};
        int i=0;
        for (Player p : gameManager.getPlayers()) {
            p.setScore(p.isActive() ? 1 : 0);
            scores[i++]=p.getScore();
        }
        endScreenModule.setScores(scores);
        endScreenModule.setTitleRankingsSprite("logo.png");
    }
}
