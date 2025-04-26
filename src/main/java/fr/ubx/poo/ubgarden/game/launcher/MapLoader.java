package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.launcher.MapEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MapLoader {

    public static MapEntity[][] loadMap(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filename));
        int height = lines.size();
        int width = lines.get(0).split(" ").length;

        MapEntity[][] map = new MapEntity[height][width];

        for (int y = 0; y < height; y++) {
            String[] symbols = lines.get(y).split(" ");
            for (int x = 0; x < width; x++) {
                map[y][x] = symbolToMapEntity(symbols[x]);
            }
        }

        return map;
    }

    private static MapEntity symbolToMapEntity(String symbol) {
        return switch (symbol) {
            case "G" -> MapEntity.Grass;
            case "B" -> MapEntity.Bombe;
            case "C" -> MapEntity.Carrots;
            case "F" -> MapEntity.Flowers;
            case "A" -> MapEntity.Apple;
            case "Pa" -> MapEntity.PoisonedApple;
            case "L" -> MapEntity.Land;
            case "T" -> MapEntity.Tree;
            case "Gr" -> MapEntity.Gardener;
            case "H" -> MapEntity.Hornets;
            case "W" -> MapEntity.Wasps;
            case "Hg" -> MapEntity.Hedgehog;
            case "Dn" -> MapEntity.DoorNextOpened;
            case "Dc" -> MapEntity.DoorNextClosed;
            case "Nh" -> MapEntity.NestHornet;
            case "Nw" -> MapEntity.NestWasp;
            default -> throw new IllegalArgumentException("Unknown symbol: " + symbol);
        };
    }
}
