package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
public class MapLevelClear extends MapLevel {

    public MapLevelClear(int width, int height) {
        super(width, height);
    }

    public static MapLevelClear fromClear(String data) {
        String[] lines = data.split("x");

        int width = lines[0].length();
        int height = lines.length;

        MapLevelClear mapLevel = new MapLevelClear(width, height);

        for (int j = 0; j < height; j++) {
            String line = lines[j];
            for (int i = 0; i < width; i++) {
                char code = line.charAt(i);
                mapLevel.set(i, j, MapEntity.fromCode(code));
            }
        }

        return mapLevel;
    }
    public List<Position> getWaspPositions() {
        List<Position> waspPositions = new ArrayList<>();
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                MapEntity entity = this.get(x, y);
                if (entity == MapEntity.Wasps || entity == MapEntity.NestWasp) {
                    waspPositions.add(new Position(1, x, y)); // Niveau 1 par défaut
                }
            }
        }
        return waspPositions;
    }

    public List<Position> getHornetPositions() {
        List<Position> hornetPositions = new ArrayList<>();
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                MapEntity entity = this.get(x, y);
                if (entity == MapEntity.Hornets || entity == MapEntity.NestHornet) {
                    hornetPositions.add(new Position(1, x, y)); // Niveau 1 par défaut
                }
            }
        }
        return hornetPositions;
    }
}
