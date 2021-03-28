import com.codingame.game.Consts;
import com.codingame.game.Vector2d;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Agent1 {

    private static Vector2d getClosestEnemyPosition(Vector2d position, Vector2d enemy) {
        Vector2d bestPos = Vector2d.zero;
        double bestDist = Double.POSITIVE_INFINITY;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Vector2d change = new Vector2d(i * Consts.MAP_X, j * Consts.MAP_Y);
                Vector2d altPos = change.add(enemy);
                double dist = position.distance(altPos);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestPos = altPos;
                }
            }
        }
        System.err.println(String.format("final dist: %.2f", bestDist));
        return bestPos;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // unitId, faction, type, health, posX, posY, velX, velY (ints for id & faction, type as Char, rest as doubles with precision 2)

        while (true) {
            int units = scanner.nextInt();
            System.err.println(String.format("%d", units));

            Vector2d enemyPos = Vector2d.zero;
            Vector2d myPos = Vector2d.zero;
            Vector2d myVel = Vector2d.zero;
            int shipID = 0;

            for (int i=0; i<units; i++) {
                int id = scanner.nextInt();
                int faction = scanner.nextInt();
                String type = scanner.next();
                double health = scanner.nextDouble();
                double posX = scanner.nextDouble();
                double posY = scanner.nextDouble();
                double velX = scanner.nextDouble();
                double velY = scanner.nextDouble();

                Vector2d pos = new Vector2d(posX, posY);
                Vector2d vel = new Vector2d(velX, velY);

                System.err.println(String.format("%d, %s, %d, %.2f, %.2f", id, type, faction, posX, posY));

                if (type.equals("S") && faction == 1) {
                    shipID = id;
                    myPos = pos;
                }

                if (type.equals("S") && faction == -1) {
                    enemyPos = pos;
                }

            }

            Vector2d enemyDir = getClosestEnemyPosition(myPos, enemyPos);
            Vector2d shootingDir = enemyDir.add(myPos.mul(-1.0));
            Vector2d fireDir = shootingDir.add(myVel.mul(-1.0));

            fireDir = fireDir.mul(10.0 / fireDir.length());

//            int newBurnX = ThreadLocalRandom.current().nextInt(-3, 3 + 1);
//            int newBurnY = ThreadLocalRandom.current().nextInt(-3, 3 + 1);

            int newBurnX = 0;
            int newBurnY = 0;

            System.out.println(String.format("%d | F %d %d | M %d %d", shipID, (int)fireDir.x, (int)fireDir.y, newBurnX, newBurnY));

        }
    }
}


    
    
    