import java.util.Properties;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("seed", "42");
        
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setGameParameters(properties);

        gameRunner.setLeagueLevel(3);

        gameRunner.addAgent(Agent1.class);
        gameRunner.addAgent(Agent2.class);


        gameRunner.start();
    }
}
