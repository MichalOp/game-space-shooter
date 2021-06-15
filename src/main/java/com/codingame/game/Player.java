package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Player extends AbstractMultiplayerPlayer {
    public Ship ship;
    public int expectedOutputLines = 0; // if we had manually detonated missiles, this should be updated accordingly
    boolean lost;
    private String message_text = "";
    Text message;

    private boolean checkValidAction(Action action, List<Unit> units) {
        try {
            Unit correct = units.stream().filter(x -> x.id == action.unitId).collect(Collectors.toList()).get(0);

            if (action.type == Action.ActionType.Move ||
                    action.type == Action.ActionType.Fire ||
                    action.type == Action.ActionType.Missile) {
                if (Double.isNaN(action.direction.x) || Double.isNaN(action.direction.y)) {
                    return false;
                }
                if (Double.isInfinite(action.direction.x) || Double.isInfinite(action.direction.y)) {
                    return false;
                }
            }

            if (action.type == Action.ActionType.Fire) {
                return (correct instanceof Ship);
            }

            if (action.type == Action.ActionType.Detonate) {
                return (correct instanceof Missile);
            }

            if (action.type == Action.ActionType.Move) {
                return ((correct instanceof Missile) || (correct instanceof Ship));
            }

            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    private double getActionDirectionValue(Scanner scanner) {
        return new BigDecimal(scanner.nextDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    List<Action> getAction(List<Unit> units) throws TimeoutException, NoSuchMethodException, InputMismatchException, NumberFormatException {
        message_text = "";
        List<Action> moves = new ArrayList<>();
        HashSet<Integer> usedUnits = new HashSet<>();
        for (String out : this.getOutputs()) {
            String[] orders = out.split("\\|");
            Scanner scanner = new Scanner(orders[0]);
            int unitId = -1;
            try {
                unitId = scanner.nextInt();
            } catch (InputMismatchException e) {
                String o = orders[0].replace(" ", "");
                if (o.charAt(0) == 'S' && o.length() == 1) {
                    unitId = ship.id;
                } else {
                    throw new InputMismatchException("");
                }
            }
            if (usedUnits.contains(unitId)) {
                throw new NoSuchMethodException("At least two orders for the same unit provided");
            }
            usedUnits.add(unitId);
            for (String order : Arrays.stream(orders).collect(Collectors.toList()).subList(1, orders.length)) {
                scanner = new Scanner(order);
                Action action = new Action();
                action.unitId = unitId;
                try {
                    switch (scanner.next()) {
                        case "A":
                        case "ACCELERATE":
                            // it's a move (for a ship or a player-controlled missile)
                            action.type = Action.ActionType.Move;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "F":
                        case "FIRE":
                            // fire a weapon in certain direction
                            action.type = Action.ActionType.Fire;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "M":
                        case "MISSILE":
                            // fire a missile
                            action.type = Action.ActionType.Missile;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "D":
                        case "DETONATE":
                            // detonate missile
                            action.type = Action.ActionType.Detonate;
                            break;
                        case "W":
                        case "WAIT":
                            // wait - this is a dummy action, but we want to have it
                            // as we can only set a constant as a number of lines we expect to get from the player
                            action.type = Action.ActionType.Wait;
                            break;
                        case "P":
                        case "PRINT":
                            // say - get a message to print
                            message_text = scanner.nextLine();
                            break;
                        default:
                            // if we wanted to print debug messages given by players this is the place for it
                            throw new NoSuchMethodException(String.format("Bad Action: %s", out));
                    }
                    if (checkValidAction(action, units)) {
                        moves.add(action);
                    } else {
                        throw new NoSuchMethodException(String.format("Invalid action: %s", out));
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        }

        return moves;
    }

    // number of lines we expect the player to give us
    @Override
    public int getExpectedOutputLines() {
        return expectedOutputLines;
    }

    public void setMessage(Referee ref, boolean init) {
        String arr[] = message_text.split("\\\\n", 6);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() > 20) {
                arr[i] = arr[i].substring(0, 18) + "...";
            }
        }

        message_text = String.join("\n", arr);
//        message_text = message_text.replace("\\n", "\n");

        if (init) {
            message = ref.graphicEntityModule.createText(message_text)
                    .setStrokeThickness(5) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(20)
                    .setFillColor(0x000000) // Setting the text color to black
                    .setX((Consts.SIDE_BAR_LEFT + Consts.SIDE_BAR_RIGHT) / 2)
                    .setY((this.getIndex() == 0 ? 0 : Consts.MAP_Y / 2) + 130 + 30 * 8)
                    .setAnchorX(0.5);
        } else {
            message.setText(message_text);
        }
    }



}
