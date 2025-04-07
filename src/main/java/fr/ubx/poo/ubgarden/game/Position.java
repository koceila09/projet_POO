package fr.ubx.poo.ubgarden.game;

public record Position (int level, int x, int y) {
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
}
