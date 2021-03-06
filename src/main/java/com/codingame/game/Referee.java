package com.codingame.game;

import java.util.*;
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
import static java.lang.Math.random;
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
            for (Unit u : unitList){
                u.tick();
            }
            updateUnits(t);
            for (Unit u : unitList) {
                u.move();
            }
            for (Unit u : unitList) {
                u.graphicsTick(t);
            }
            graphicEntityModule.commitWorldState(t);
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
        double thickness = 0.03;
        graphicEntityModule.createSprite().setImage("grad.png").setScaleY(Consts.MAP_Y/400.0).setScaleX(thickness);
        graphicEntityModule.createSprite().setImage("grad.png").setScaleY(Consts.MAP_X/400.0).setScaleX(thickness).setRotation(Math.PI/2).setX(Consts.MAP_X);
        graphicEntityModule.createSprite().setImage("grad.png").setScaleY(Consts.MAP_X/400.0).setScaleX(thickness).setRotation(-Math.PI/2).setY(Consts.MAP_Y);
        graphicEntityModule.createSprite().setImage("grad.png").setScaleY(Consts.MAP_Y/400.0).setScaleX(thickness).setRotation(Math.PI).setX(Consts.MAP_X).setY(Consts.MAP_Y);

        sidebar = graphicEntityModule.createSprite().setImage("sidebar.png").setX(Consts.MAP_X);

        Random generator = new Random(gameManager.getSeed());
        int start_position = (int)Math.floor(generator.nextDouble()*3.0);

        boolean missile_enabled = gameManager.getLeagueLevel() > 1;

        for (Player p : gameManager.getPlayers()) {
            p.missile_enabled = missile_enabled;
            p.setMessage(this, true);
            int faction = p.getIndex();

            Vector2d initial_position = new Vector2d(faction == 0 ? WIDTH / 4.0 : WIDTH / 4.0 * 3,
                    faction == 0 ? HEIGHT * Consts.START_POSITION_0[start_position] : HEIGHT * Consts.START_POSITION_1[start_position]);

            if (gameManager.getLeagueLevel() == 1) {
                initial_position.x = faction == 0 ? WIDTH / 2.0 - 350 : WIDTH / 2.0 + 350;
                initial_position.y = HEIGHT / 2.0;
            }

            Ship s = new Ship(initial_position, Vector2d.zero, faction, this, p.getNicknameToken(), p.getAvatarToken());


            addUnit(s);
            p.ship = s;
            p.expectedOutputLines += 1;
            s.graphicsTick(-1);
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

            } catch (TimeoutException | NoSuchMethodException | InputMismatchException | IllegalArgumentException e) {
                String message = String.format("%s eliminated! Reason: ", p.getNicknameToken());
                if (e instanceof TimeoutException) {
                    message += String.format("Timeout! (%d lines expected)", p.getExpectedOutputLines());
                } else {
                    message += e.getMessage();
                }
                gameManager.addToGameSummary(message);
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
