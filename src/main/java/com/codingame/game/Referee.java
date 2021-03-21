package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.view.AnimatedEventModule;
import com.codingame.view.ViewerEvent;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    private static int WIDTH = 1920;
    private static int HEIGHT = 1080;
    private static int BALL_RADIUS = 20;
    private static int PADDLE_WIDTH = 15;
    private static int PADDLE_HEIGHT = 150;

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject public GraphicEntityModule graphicEntityModule;
    @Inject private AnimatedEventModule animatedEventModule;

    private int ballX, ballY;
    private int ballVX, ballVY;
    private Circle ball;

    private int unitId = 0;
    private List<Unit> unitList;
    private List<Unit> newUnitList;

    public final List<Unit> GetUnits(){
        return unitList;
    }

    public void addUnit(Unit u){
        newUnitList.add(u);
    }

    public int getId(){
        return unitId++;
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private void sendPlayerInputs() {

        //TODO: send input to players

        List<Player> allPlayers = gameManager.getPlayers();
        for (Player p : gameManager.getActivePlayers()) {
            p.sendInputLine(String.valueOf(p.y));
            p.sendInputLine(String.valueOf(allPlayers.get((p.getIndex() + 1) % 2).y));
            p.sendInputLine(String.format("%d %d", ballX, ballY));
            p.sendInputLine(String.format("%d %d", ballVX, ballVY));
            p.execute();
        }
    }

    private double min(double... values) {
        double m = values[0];
        for (double v : values) {
            m = Math.min(m, v);
        }
        return m;
    }

    private void moveBall() {
        double t = 0;
        while (t < 1) {
            double timeCollisionTop = ballVY < 0 ? (BALL_RADIUS - ballY) / (double) ballVY : 1;
            double timeCollisionBottom = ballVY > 0 ? (HEIGHT - BALL_RADIUS - ballY) / (double) ballVY : 1;
            double timeCollisionLeft = ballVX < 0 ? (BALL_RADIUS + PADDLE_WIDTH - ballX) / (double) ballVX : 1;
            double timeCollisionRight = ballVX > 0 ? (WIDTH - PADDLE_WIDTH - BALL_RADIUS - ballX) / (double) ballVX : 1;

            if (ballX <= BALL_RADIUS + PADDLE_WIDTH && gameManager.getPlayer(0).lost) {
                timeCollisionLeft = 1;
            }
            if (ballX >= WIDTH - BALL_RADIUS - PADDLE_WIDTH && gameManager.getPlayer(1).lost) {
                timeCollisionRight = 1;
            }

            double delta = min(timeCollisionTop, timeCollisionBottom, timeCollisionLeft, timeCollisionRight, 1 - t);
            t += delta;

            ballX += ballVX * delta;
            ballY += ballVY * delta;

            if (ballVY < 0 && ballY <= BALL_RADIUS || ballVY > 0 && this.ballY >= HEIGHT - BALL_RADIUS) {
                this.ballVY *= -1;
            }

            if (ballVX < 0 && ballX <= BALL_RADIUS + PADDLE_WIDTH) {
                Player p = gameManager.getPlayer(0);
                double paddleY = (p.previousY * (1 - t)) + p.y * t;
                if (ballY > paddleY - PADDLE_HEIGHT / 2 && ballY < paddleY + PADDLE_HEIGHT / 2) {
                    ballVX *= -1;

                    gameManager.addTooltip(new Tooltip(p.getIndex(), "Ping"));
                    
                    ViewerEvent ev = animatedEventModule.createAnimationEvent("Ping", t);
                    ev.params.put("player", 0);
                    ev.params.put("x", ballX);
                    ev.params.put("y", ballY);

                } else {
                    p.lost = true;
                }
            }
            if (ballVX > 0 && ballX >= WIDTH - BALL_RADIUS - PADDLE_WIDTH) {
                Player p = gameManager.getPlayer(1);
                double paddleY = (p.previousY * (1 - t)) + p.y * t;
                if (ballY > paddleY - PADDLE_HEIGHT / 2 && ballY < paddleY + PADDLE_HEIGHT / 2) {
                    ballVX *= -1;
                    gameManager.addTooltip(new Tooltip(p.getIndex(), "Pong"));

                    ViewerEvent ev = animatedEventModule.createAnimationEvent("Ping", t);
                    ev.params.put("player", 1);
                    ev.params.put("x", ballX);
                    ev.params.put("y", ballY);
                } else {
                    p.lost = true;
                }
            }

            ball.setX(ballX).setY(ballY);
            graphicEntityModule.commitEntityState(t, ball);
        }
    }

    void updateUnits(){
        //purge dead units
        unitList = unitList.stream().filter(x -> x.health > 0).collect(Collectors.toList());
        //add new units
        unitList.addAll(newUnitList);
        newUnitList = new ArrayList<>();
    }

    void doTurn(){
        double t = 0;
        while(t < 1){
            for (Unit u : unitList){
                u.graphicsTick(t);
            }
            for (Unit u : unitList){
                u.tick();
            }
            // TODO: damage application
            updateUnits();
            for (Unit u : unitList){
                u.move();
            }

            t += 0.1;
        }
    }

    @Override
    public void init() {
        unitList = new ArrayList<>();
        newUnitList = new ArrayList<>();

        gameManager.setFrameDuration(300);
        
        graphicEntityModule.createSprite().setImage("Background.jpg").setAnchor(0);

        for (Player p : gameManager.getPlayers()) {
            int faction = p.getIndex();
            Ship s = new Ship(new Vector2d(faction == 0 ? WIDTH/4 : WIDTH/4*3,HEIGHT/2), Vector2d.zero, faction, this);

            addUnit(s);
            p.ship = s;
        }
        updateUnits();
    }

    @Override
    public void gameTurn(int turn) {
        // Send new inputs with the updated positions
        sendPlayerInputs();
        System.out.println(turn);

        // Update new positions
        // TODO: receiving orders from the player
        for (Player p : gameManager.getActivePlayers()) {
            int deltaMove;
            try {

                deltaMove = p.getAction() - p.y;
                p.ship.setBurn(new Vector2d(10,0));
                p.ship.fire(new Vector2d(1,1));
//                deltaMove = clamp(deltaMove, -120, 120);
//                int newPosY = p.y + deltaMove;
//
//                p.previousY = p.y;
//                p.y = clamp(newPosY, PADDLE_HEIGHT / 2, HEIGHT - PADDLE_HEIGHT / 2);
//                p.paddle.setY(p.y - PADDLE_HEIGHT / 2);
//                p.paddle.setY2(p.y + PADDLE_HEIGHT / 2);
            } catch (NumberFormatException | TimeoutException e) {
                p.deactivate("Eliminated!");
            }
        }

        doTurn();

        for (Player p : gameManager.getActivePlayers()) {
            if(p.ship.health <= 0){
                p.deactivate();
            }
        }

        if (gameManager.getActivePlayers().size() < 2 || turn > 200) {
            gameManager.endGame();
        }
    }

    @Override
    public void onEnd() {
        for (Player p : gameManager.getPlayers()) {
            p.setScore(p.isActive() ? 1 : 0);
        }
    }
}
