package fr.ubx.poo.ubgarden.game.launcher;

public class MapLevelCompressed extends MapLevel {

    public MapLevelCompressed(int width, int height) {
        super(width, height);
    }

    public static MapLevelCompressed fromCompressed(String data) {
        String[] lines = data.split("x");

        int width = decompressLine(lines[0]).length();
        int height = lines.length;

        MapLevelCompressed mapLevel = new MapLevelCompressed(width, height);

        for (int j = 0; j < height; j++) {
            String line = decompressLine(lines[j]);
            for (int i = 0; i < width; i++) {
                char code = line.charAt(i);
                mapLevel.set(i, j, MapEntity.fromCode(code));
            }
        }

        return mapLevel;
    }

    private static String decompressLine(String line) {
        StringBuilder result = new StringBuilder();
        int n = line.length();
        for (int i = 0; i < n; i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                int repeat = c - '0';
                if (i + 1 >= n) {
                    throw new RuntimeException("Invalid compressed format");
                }
                char repeatedChar = line.charAt(i + 1);
                result.append(String.valueOf(repeatedChar).repeat(repeat));
                i++; // On saute la lettre répétée
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
