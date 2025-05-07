package fr.ubx.poo.ubgarden.game;

import java.util.HashMap;
import java.util.Map;

public class World {

    private final Map<Integer, Level> levels = new HashMap<>();
    private final java.util.Map<Integer, Map> grids = new HashMap<>();

    private int currentLevel = 1;

    public World(int levels) {
        if (levels < 1) throw new IllegalArgumentException("Levels must be greater than 0");
    }

    public int currentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Level getGrid(int level) {
        return levels.get(level);
    }

    public Level getGrid() {
        return getGrid(currentLevel);
    }





    public void put(int level, Level levelGrid) {
        this.levels.put(level, levelGrid);
    }

    public void put2(int level, Map grid) {
        this.grids.put(level, grid);
    }

    public Level get(int level) {
        return levels.get(level);
    }
}
