package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.util.List;
import java.util.Properties;

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
            // Charger la première map (level1.txt)
            MapEntity[][] entities1 = MapLoader.loadMap(file.getPath());

            int width = entities1[0].length;
            int height = entities1.length;

            MapLevel mapLevel1 = new MapLevel(width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    mapLevel1.set(x, y, entities1[y][x]);
                }
            }

            // Récupérer les positions importantes du niveau 1
            Position gardenerPosition = mapLevel1.getGardenerPosition();
            Position waspPosition = mapLevel1.getwaspPosition();
            Position hornetPosition = mapLevel1.gethornetPosition();

            if (gardenerPosition == null)
                throw new RuntimeException("Gardener not found");
            if (waspPosition == null)
                throw new RuntimeException("Wasp not found");
            if (hornetPosition == null)
                throw new RuntimeException("Hornet not found");

            // Charger la deuxième map (level2.txt)
            File fileLevel2 = new File("src/main/resources/maps/level2.txt");
            MapEntity[][] entities2 = MapLoader.loadMap(fileLevel2.getPath());

            int width2 = entities2[0].length;
            int height2 = entities2.length;

            MapLevel mapLevel2 = new MapLevel(width2, height2);
            for (int y = 0; y < height2; y++) {
                for (int x = 0; x < width2; x++) {
                    mapLevel2.set(x, y, entities2[y][x]);
                }
            }

            // Configuration vide pour le moment
            Properties emptyConfig = new Properties();
            Configuration configuration = getConfiguration(emptyConfig);

            // Créer le monde avec 2 niveaux
            World world = new World(2);

            List<Position> waspPositions = List.of(waspPosition);
            List<Position> hornetPositions = List.of(hornetPosition);

            // Créer l'objet Game
            Game game = new Game(world, configuration, gardenerPosition, waspPositions, hornetPositions);

            // Associer les niveaux au monde
            Map level1 = new Level(game, 1, mapLevel1);
            Map level2 = new Level(game, 2, mapLevel2);

            world.put(1, level1);
            world.put(2, level2);

            return game;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load map from file", e);
        }
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
}
