package fr.ubx.poo.ubgarden.game.launcher;

public enum MapEntity {
    PoisonedApple('-'),
    Apple('+'),
    Carrots('F'),
    Flowers('O'),
    Grass('G'),
    Land('L'),
    Tree('T'),
    Bombe('K'),
    NestWasp('n'),

    Gardener('P'),
    Wasps('W'),

    Hornets('Z'),
    Hedgehog('H'),

    DoorPrevOpened('<'),
    DoorNextOpened('>'),
    DoorNextClosed('D'),
    NestHornet('N');

    private final char code;

    MapEntity(char c) {
        this.code = c;
    }

    public static MapEntity fromCode(char c) {
        for (MapEntity mapEntity : values()) {
            if (mapEntity.code == c)
                return mapEntity;
        }
        throw new MapException("Invalid character " + c);
    }

    @Override
    public String toString() {
        return Character.toString(code);
    }

}
