package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.*;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;

import java.util.Collection;
import java.util.HashMap;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.DoorNextClosed;
import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.DoorNextOpened;

public class Level implements Map {

    private final int level;
    private final int width;

    private final int height;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Hedgehog:
                        decors.put(position, new Hedgehog(position));
                        break;
                    case DoorNextClosed: {
                        Decor doorOpened = new DoorNextOpened(position); // 1. Créer la porte ouverte
                        DoorNextClose doorClosed = new DoorNextClose(position, doorOpened); // 2. Créer le bonus porte fermée
                        doorOpened.setBonus(doorClosed); // 3. Mettre le bonus sur la porte ouverte
                        decors.put(position, doorOpened); // 4. Mettre la porte ouverte sur la map
                        break;
                    }

                    case DoorPrevOpened:
                        decors.put(position, new DoorPrevOpened(position));
                        break;
                    case DoorNextOpened:
                        decors.put(position, new DoorNextOpened(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case Land:
                        decors.put(position, new Land(position));
                        break;
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case NestWasp:
                        decors.put(position, new NestWasp(position));
                        break;
                    case NestHornet:
                        decors.put(position, new NestHornet(position));
                        break;
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new EnergyBoost(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case PoisonedApple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Bombe: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Bombe_insecticide(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Carrots: {
                        Decor grass = new Land(position);
                        grass.setBonus(new Carrots(position, grass));
                        decors.put(position, grass);
                        break;

                    }
                    case Gardener:
                    case Wasps:
                    case Hornets:
                        decors.put(position, new Grass(position)); // mettre de l'herbe à leur place
                        break;

                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
    }

    public void put(Position position, Decor decor) {
        decors.put(position, decor);
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }


    @Override
    public boolean inside(Position position) {
        int x = position.x();
        int y = position.y();
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    public java.util.Map<Position, Decor> getDecors() {
        return decors;
    }




}
