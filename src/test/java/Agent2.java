import com.codingame.game.Vector2d;

import java.util.Scanner;

public class Agent2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // unitId, faction, type, health, posX, posY, velX, velY (ints for id & faction, type as Char, rest as doubles with precision 2)
        int cnt = 0;
        int a=0;
        int[] x={0, 1, 1, 1, 0, -1, -1, -1};
        int[] y={1, 1, 0, -1, -1, -1, 0, 1};
        while (true) {
            int units = scanner.nextInt();
            System.err.printf("%d%n", units);
            for (int i=0; i<units; i++) {
                int id = scanner.nextInt();
                int faction = scanner.nextInt();
                String type = scanner.next();
                double health = scanner.nextDouble();
                double posX = scanner.nextDouble();
                double posY = scanner.nextDouble();
                double velX = scanner.nextDouble();
                double velY = scanner.nextDouble();
                double cooldown = scanner.nextDouble();

                Vector2d pos = new Vector2d(posX, posY);
                Vector2d vel = new Vector2d(velX, velY);

                System.err.printf("%d, %s, %d, %.2f, %.2f%n", id, type, faction, posX, posY);


                if (type.equals("S") && faction == 1) {
                    if(a<8)System.out.println(String.format(" S | F %d %d | A %d %d  | M  %d %d", 3, 0, 0, cnt %2 > 0 ? 1 : -1, x[a], y[a]));
                    else{
                        System.out.println(String.format(" S | F %d %d | A %d %d  ", 3, 0, cnt >0 ? 1 :0, 0));
                    }
                    cnt++;
                    a++;
                }
                if (type.equals("M") && faction == 1) {
                    double r = Math.random();
//                    System.err.println(String.format("%d | A %d %d | %s", id, 10, 10, r < 0.01 ? "D" : ""));
                    System.out.println(String.format("%d | A %d %d | %s", id,-1, -1, ""));
                }
            }

        }
    }
}
