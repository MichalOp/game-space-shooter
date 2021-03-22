package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
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
            // for now we don't have any units that can be detonated manually,
            // but if we have them in the future this is a place for checking validity of detonation

            if (action.type == Action.ActionType.Move || action.type == Action.ActionType.Fire) {
                // for now bullet max acceleration is the same as for ship
                if (abs(action.direction.x) > Consts.SHIP_MAX_ACCELERATION) {
                    return false;
                }
                if (abs(action.direction.y) > Consts.SHIP_MAX_ACCELERATION) {
                    return false;
                }
            }

            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    List<Action> getAction(List<Unit> units) throws TimeoutException, NoSuchMethodException, InputMismatchException {
        List<Action> moves = new ArrayList<>();
        for (String out : this.getOutputs()) {
            Scanner scanner = new Scanner(out);
            Action action = new Action();
            switch (scanner.next()) {
                case "M":
                    // it's a move (for a ship or a player-controlled missile)
                    action.unitId = scanner.nextInt();
                    action.type = Action.ActionType.Move;
                    action.direction.x = scanner.nextInt();
                    action.direction.y = scanner.nextInt();
                    break;
                case "F":
                    // fire a weapon in certain direction
                    action.unitId = scanner.nextInt();
                    action.type = Action.ActionType.Fire;
                    action.direction.x = scanner.nextInt();
                    action.direction.y = scanner.nextInt();
                    break;
                case "D":
                    // detonate missile
                    action.unitId = scanner.nextInt();
                    action.type = Action.ActionType.Detonate;
                    break;
                case "W":
                    // wait - this is a dummy action, but we want to have it
                    // as we can only set a constant as a number of lines we expect to get from the player
                    action.unitId = scanner.nextInt();
                    action.type = Action.ActionType.Wait;
                    break;
                default:
                    throw new NoSuchMethodException(String.format("Bad Action: %s", out));
            }
            if (checkValidAction(action, units)) {
                moves.add(action);
            } else {
                throw new NoSuchMethodException(String.format("Invalid action: %s", action.toString() ));
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
