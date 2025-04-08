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

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) {
        return null;
    }

    public Game load() throws RuntimeException {
        // Charger une configuration vide par défaut
        Properties emptyConfig = new Properties();

        // Récupérer les informations du niveau par défaut
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        Position waspPosition = mapLevel.getwaspPosition();
        Position hornetPosition = mapLevel.gethornetPosition();

        // Vérifier que les positions sont valides
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        if (waspPosition == null)
            throw new RuntimeException("Wasp not found");
        if (hornetPosition == null)
            throw new RuntimeException("Hornet not found");

        // Charger la configuration du jeu
        Configuration configuration = getConfiguration(emptyConfig);

        // Créer le monde
        World world = new World(1);

        // Convertir les positions individuelles en listes
        List<Position> waspPositions = List.of(waspPosition);
        List<Position> hornetPositions = List.of(hornetPosition);

        // Créer une instance de Game avec les listes de positions
        Game game = new Game(world, configuration, gardenerPosition, waspPositions, hornetPositions);

        // Ajouter le niveau au monde
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);

        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
