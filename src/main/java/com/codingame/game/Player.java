package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


public class Player extends AbstractMultiplayerPlayer {
    public Ship ship;
    public int expectedOutputLines = 0;
    boolean lost;
    private String message_text = "";
    Text message;
    private String checkValidActionErrorMessage = "";

    private boolean checkValidAction(Action action, List<Unit> units) {
        try {
            Unit correct = units.stream().filter(x -> x.id == action.unitId).collect(Collectors.toList()).get(0);

            if (action.type == Action.ActionType.Move ||
                    action.type == Action.ActionType.Fire ||
                    action.type == Action.ActionType.Missile) {
                if (Double.isNaN(action.direction.x) || Double.isNaN(action.direction.y)) {
                    checkValidActionErrorMessage = "Action direction is NaN";
                    return false;
                }
                if (Double.isInfinite(action.direction.x) || Double.isInfinite(action.direction.y)) {
                    checkValidActionErrorMessage = "Action direction is infinite";
                    return false;
                }
            }

            if (action.type == Action.ActionType.Fire) {
                boolean instance = (correct instanceof Ship);
                if (!instance) {
                    checkValidActionErrorMessage = "Only a ship can fire bullets";
                }
                return instance;
            }

            if (action.type == Action.ActionType.Detonate) {
                boolean instance = (correct instanceof Missile);
                if (!instance) {
                    checkValidActionErrorMessage = "Only a missile can be detonated";
                }
                return instance;
            }

            if (action.type == Action.ActionType.Move) {
                boolean instance = ((correct instanceof Missile) || (correct instanceof Ship));
                if (!instance) {
                    checkValidActionErrorMessage = "Bullets can't be accelerated";
                }
                return instance;
            }

            return true;
        } catch (IndexOutOfBoundsException e) {
            checkValidActionErrorMessage = "Received order for not-owned unit with id " + action.unitId;
            return false;
        }
    }

    private double getActionDirectionValue(Scanner scanner) {
        return new BigDecimal(scanner.nextDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    List<Action> getAction(List<Unit> units) throws TimeoutException, NoSuchMethodException,
            InputMismatchException, IllegalArgumentException {
        message_text = "";
        List<Action> moves = new ArrayList<>();
        HashSet<Integer> usedUnits = new HashSet<>();
        for (String out : this.getOutputs()) {
            String[] orders = out.split("\\|");
            String firstPart = orders[0];
            Scanner scanner = new Scanner(firstPart);
            int unitId = -1;
            try {
                unitId = scanner.nextInt();
            } catch (NoSuchElementException e) {
                String maybeShip = scanner.next();
                if (maybeShip.length() == 1 && maybeShip.charAt(0) == 'S') {
                    unitId = ship.id;
                } else {
                    throw new InputMismatchException("\"" + maybeShip + "\"" + " is not a correct unit identifier (" + out + ")");
                }
            }
            if (scanner.hasNext()) {
                throw new InputMismatchException(String.format("Some chars after unit id found (missing |?) (%s)", out));
            }
            if (usedUnits.contains(unitId)) {
                throw new NoSuchMethodException(String.format("Two orders for the same unit (id %d) provided (%s)", unitId, out));
            }
            usedUnits.add(unitId);
            List<String> ordersList = Arrays.stream(orders).collect(Collectors.toList());
            if (ordersList.size() < 2) {
                throw new NoSuchMethodException(String.format("No orders for unit (id %d) provided (%s)", unitId, out));
            }
            for (String order : ordersList.subList(1, orders.length)) {
                if (order.replaceAll("\\s+", "").length() < 1) {
                    continue;
                }
                scanner = new Scanner(order);
                Action action = new Action();
                action.unitId = unitId;
                try {
                    String actionName = scanner.next();
                    switch (actionName) {
                        case "A":
                        case "ACCELERATE":
                            // it's a move (for a ship or a player-controlled missile)
                            action.type = Action.ActionType.Move;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            if (scanner.hasNext()) {
                                throw new IllegalArgumentException(String.format("Some redundant chars found (missing |?) (%s)", out));
                            }
                            break;
                        case "F":
                        case "FIRE":
                            // fire a weapon in certain direction
                            action.type = Action.ActionType.Fire;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            if (scanner.hasNext()) {
                                throw new IllegalArgumentException(String.format("Some redundant chars found (missing |?) (%s)", out));
                            }
                            break;
                        case "M":
                        case "MISSILE":
                            // fire a missile
                            action.type = Action.ActionType.Missile;
                            action.direction.x = getActionDirectionValue(scanner);
                            action.direction.y = getActionDirectionValue(scanner);
                            if (scanner.hasNext()) {
                                throw new IllegalArgumentException(String.format("Some redundant chars found (missing |?) (%s)", out));
                            }
                            break;
                        case "D":
                        case "DETONATE":
                            // detonate missile
                            action.type = Action.ActionType.Detonate;
                            if (scanner.hasNext()) {
                                throw new IllegalArgumentException(String.format("Some redundant chars found (missing |?) (%s)", out));
                            }
                            break;
                        case "W":
                        case "WAIT":
                            // wait - this is a dummy action, but we want to have it
                            // as we can only set exact number of lines we expect to get from the player
                            action.type = Action.ActionType.Wait;
                            if (scanner.hasNext()) {
                                throw new IllegalArgumentException(String.format("Some redundant chars found (missing |?) (%s)", out));
                            }
                            break;
                        case "P":
                        case "PRINT":
                            // print - get a message to print
                            message_text = scanner.nextLine();
                            break;
                        default:
                            // if we wanted to print debug messages given by players this is the place for it
                            throw new NoSuchMethodException(String.format("Not recognized action %s (%s)", actionName, out));
                    }
                    checkValidActionErrorMessage = "";
                    if (checkValidAction(action, units)) {
                        moves.add(action);
                    } else {
                        throw new NoSuchMethodException(String.format("%s (%s)", checkValidActionErrorMessage, out));
                    }
                } catch (NoSuchElementException | NumberFormatException e) {
                    if (e instanceof NumberFormatException) {
                        throw new NumberFormatException("Badly formatted double values " + "(" + out + ")");
                    } else {
                        throw new NoSuchMethodException("Missing or wrong value provided " + "(" + out + ")");
                    }
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
