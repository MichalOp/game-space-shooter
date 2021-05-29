package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Player extends AbstractMultiplayerPlayer {
    public Ship ship;
    public int expectedOutputLines = 0; // if we had manually detonated missiles, this should be updated accordingly
    boolean lost;
    // rest of the units is controlled by referee,
    // probably at some point we would like more of the stuff to be stored here (non-autonomous missiles?)

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
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private double getActionDirectionValue(Scanner scanner) {
        return new BigDecimal(scanner.nextDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    List<Action> getAction(List<Unit> units) throws TimeoutException, NoSuchMethodException, InputMismatchException, NumberFormatException {
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
                if (o.charAt(0) == 'S') {
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
                            // it's a move (for a ship or a player-controlled missile)
                            action.type = Action.ActionType.Move;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "F":
                            // fire a weapon in certain direction
                            action.type = Action.ActionType.Fire;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "M":
                            // fire a missile
                            action.type = Action.ActionType.Missile;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            break;
                        case "D":
                            // detonate missile
                            action.type = Action.ActionType.Detonate;
                            break;
                        case "W":
                            // wait - this is a dummy action, but we want to have it
                            // as we can only set a constant as a number of lines we expect to get from the player
                            action.type = Action.ActionType.Wait;
                            break;
                        default:
                            // if we wanted to print debug messages given by players this is the place for it
                            throw new NoSuchMethodException(String.format("Bad Action: %s", out));
                    }
                    if (checkValidAction(action, units)) {
                        moves.add(action);
                    } else {
                        throw new NoSuchMethodException(String.format("Invalid action: %s", action.toString()));
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
}
