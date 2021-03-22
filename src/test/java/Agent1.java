import com.codingame.game.Vector2d;

import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        scanner.useDelimiter(" ");

        // unitId, faction, type, health, posX, posY, velX, velY (ints for id & faction, type as Char, rest as doubles with precision 2)

        while (true) {
//            System.err.println(scanner.nextLine());
            int units = scanner.nextInt();
            System.err.println(String.format("%d", units));
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
                    System.out.println(String.format("M %d %d %d", id, 10, 0));
                    System.out.println(String.format("F %d %d %d", id, 1, 1));
                }
            }

        }
    }
}


    
    
    