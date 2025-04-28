package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }
    public Game load(File file) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));

            Configuration configuration = getConfiguration(properties);

            boolean compression = booleanProperty(properties, "compression", false);
            int levels = integerProperty(properties, "levels", 1);

            World world = new World(levels);

            for (int levelNumber = 1; levelNumber <= levels; levelNumber++) {
                String levelData = properties.getProperty("level" + levelNumber);
                if (levelData == null)
                    throw new RuntimeException("Missing level" + levelNumber + " data");

                MapLevel mapLevel = compression ? MapLevelCompressed.fromCompressed(levelData) : MapLevelClear.fromClear(levelData);

                Level map = new Level(null, levelNumber, mapLevel);

                world.put(levelNumber, map);
            }

            // Positionnement initial du jardinier
            MapLevelClear firstLevel = MapLevelClear.fromClear(properties.getProperty("level1"));

            Position gardenerPosition = firstLevel.getGardenerPosition();
            List<Position> waspPositions = firstLevel.getWaspPositions();
            List<Position> hornetPositions = firstLevel.getHornetPositions();


            if (gardenerPosition == null)
                throw new RuntimeException("Gardener not found in level 1");




            Game game = new Game(world, configuration, gardenerPosition, waspPositions, hornetPositions);

            return game;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load the game from file: " + e.getMessage());
        }
    }



    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
}
