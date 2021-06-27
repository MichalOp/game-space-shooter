package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
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
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.view.AnimatedEventModule;
import com.codingame.view.ViewerEvent;
import com.google.inject.Inject;

import static java.lang.Math.max;
import static java.lang.Thread.sleep;

public class Referee extends AbstractReferee {
    private static int WIDTH = 1700;
    private static int HEIGHT = 1080;

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject public GraphicEntityModule graphicEntityModule;
    @Inject private AnimatedEventModule animatedEventModule;
    @Inject public TooltipModule tooltips;
    @Inject public EndScreenModule endScreenModule;
    @Inject ToggleModule toggleModule;
    
    private int unitId = 0;
    private List<Unit> unitList;

    public final List<Unit> getUnits() {
        return unitList;
    }

    public Sprite sidebar;

    public int addUnit(Unit u) {
        unitList.add(u);
        return u.id;
    }

    public int getId() {
        return unitId++;
    }

    private int clamp(int val, int min, int max) {
        return max(min, Math.min(max, val));
    }

    private void sendPlayerInputs() {

        // input for players:
        // number of lines
        // unitId, faction, type, health, posX, posY, velX, velY, gunCooldown (ints for id & faction, rest as doubles with precision 2)
        // gunCooldown is from 0 to maxCooldown for ship, else -1

        for (Player p : gameManager.getActivePlayers()) {
            p.sendInputLine(String.valueOf(getUnits().size()));
            for (Unit u : getUnits()) {
                double gunCooldown = -1;
                if (u instanceof Ship) {
                    Ship ship = (Ship) u;
                    gunCooldown = max(0.0, Consts.GUN_COOLDOWN - ship.gunCooldown);
                }
                p.sendInputLine(String.format("%d %d %s %.2f %.2f %.2f %.2f %.2f %.2f",
                        u.id, u.faction == p.getIndex() ? 1 : -1, u.getUnitType(),
                        u.health, u.position.x, u.position.y, u.velocity.x, u.velocity.y,
                        gunCooldown));
            }
            p.execute();
        }
    }

    public void registerExplosion(Vector2d position, double radius, double damage) {
        for (Unit u : unitList) {
            double distance = u.position.distance(position);
            if (distance <= radius) {
                u.health -= damage * (radius - distance) / radius;

            }
        }
    }

    void updateUnits(double t) {
        List<Unit> dead = unitList.stream().filter(x -> x.health <= 0).collect(Collectors.toList());
        unitList = unitList.stream().filter(x -> x.health > 0).collect(Collectors.toList());
        while (!dead.isEmpty()) {
            for (Unit u : dead) {
                u.onDeath(t);
                // it probably should be somewhere else
                if (u instanceof Missile) {
                    gameManager.getPlayer(u.faction).expectedOutputLines -= 1;
                }
            }
            dead = unitList.stream().filter(x -> x.health <= 0).collect(Collectors.toList());
            unitList = unitList.stream().filter(x -> x.health > 0).collect(Collectors.toList());
        }
    }

    void doTurn() {
        double t = 0;
        graphicEntityModule.commitWorldState(0);
        while (t < 1 - 0.000001) {
            t += Consts.TIME_DELTA;
            for (Unit u : unitList) {
                u.move();
            }
            for (Unit u : unitList) {
                u.graphicsTick(t);
            }
            graphicEntityModule.commitWorldState(t);
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
        gameManager.setMaxTurns(100);
        gameManager.setFirstTurnMaxTime(1000);
        gameManager.setTurnMaxTime(100);

        graphicEntityModule.createSprite().setImage("Background.jpg").setAnchor(0);
        graphicEntityModule.createLine().setLineColor(0xADD8E6 ).setX(0).setX2(0).setLineWidth(5).setY(0).setY2(Consts.MAP_Y);
        graphicEntityModule.createLine().setLineColor(0xADD8E6 ).setX(Consts.MAP_X-2).setX2(Consts.MAP_X-2).setLineWidth(5).setY(0).setY2(Consts.MAP_Y);
        graphicEntityModule.createLine().setLineColor(0xADD8E6 ).setX(0).setX2(Consts.MAP_X).setLineWidth(5).setY(Consts.MAP_Y-2).setY2(Consts.MAP_Y-2);
        graphicEntityModule.createLine().setLineColor(0xADD8E6 ).setX(0).setX2(Consts.MAP_X).setLineWidth(5).setY(0).setY2(0);



        sidebar = graphicEntityModule.createSprite().setImage("sidebar.png").setX(Consts.MAP_X);

        for (Player p : gameManager.getPlayers()) {
            p.setMessage(this, true);
            int faction = p.getIndex();
            Ship s = new Ship(new Vector2d(faction == 0 ? WIDTH / 4 : WIDTH / 4 * 3, HEIGHT / 2), Vector2d.zero, faction, this, p.getNicknameToken());

            addUnit(s);
            p.ship = s;
            p.expectedOutputLines += 1;
        }
    }

    public void printMessages() {
        for (Player p : gameManager.getActivePlayers()) {
            p.setMessage(this, false);
        }
    }

    @Override
    public void gameTurn(int turn) {
        // Send new inputs with the updated positions
        sendPlayerInputs();


        // Update new positions
        for (Player p : gameManager.getActivePlayers()) {
            try {
                List<Unit> playersUnits = unitList.stream()
                        .filter(x -> x.faction == p.getIndex())
                        .collect(Collectors.toList());

                List<Action> actions = p.getAction(playersUnits);
                // we need to do them in a specific order, e.g. spawn a missile before moving it
                Collections.sort(actions);

                for (Action action : actions) {
                    if (action.type == Action.ActionType.Missile) {
                        int missileID = p.ship.launchMissile();
                        // there was an actual missile launch
                        if (missileID > 0) {
                            p.expectedOutputLines += 1;
                            List<Unit> units = unitList.stream()
                                    .filter(x -> x.id == missileID)
                                    .collect(Collectors.toList());
                            Unit unit = units.get(0);
                            Missile missile = (Missile) unit;
                            missile.setBurn(action.direction);
                        }
                    }
                    if (action.type == Action.ActionType.Move) {
                        List<Unit> units = unitList.stream()
                                .filter(x -> x.id == action.unitId)
                                .collect(Collectors.toList());
                        Unit unit = units.get(0);
                        if (unit instanceof Ship) {
                            p.ship.setBurn(action.direction);
                        }
                        if (unit instanceof Missile) {
                            Missile missile = (Missile) unit;
                            missile.setBurn(action.direction);
                        }
                    }
                    if (action.type == Action.ActionType.Fire) {
                        p.ship.fire(action.direction);
                    }
                    if (action.type == Action.ActionType.Detonate) {
                        List<Unit> units = unitList.stream()
                                .filter(x -> x.id == action.unitId)
                                .collect(Collectors.toList());
                        Unit unit = units.get(0);
                        Missile missile = (Missile) unit;
                        missile.detonate();
                    }
                }

            } catch (TimeoutException | NoSuchMethodException | InputMismatchException | NumberFormatException e) {
                String message = String.format("%s eliminated! Reason: ", p.getNicknameToken());
                if (e instanceof TimeoutException) {
                    message += String.format("Timeout! (%d lines expected)", p.getExpectedOutputLines());
                } else if (e instanceof NoSuchMethodException) {
                    message += e.getMessage();
                } else {
                    message += "Bad input!";
                }
                p.deactivate(message);
            }
        }

        printMessages();

        doTurn();

        for (Player p : gameManager.getActivePlayers()) {
            if (p.ship.health <= 0) {
                p.ship.health = 0;
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
        for (Unit u : unitList) {
            u.graphicsTick(1);
        }
        int[] scores = {0, 0};
        int i = 0;
        for (Player p : gameManager.getPlayers()) {
            p.setScore(p.isActive() ? 1 : 0);
            scores[i++] = p.getScore();
        }
        endScreenModule.setScores(scores);
        endScreenModule.setTitleRankingsSprite("logo.png");
    }
}
