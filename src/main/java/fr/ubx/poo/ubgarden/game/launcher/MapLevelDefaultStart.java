package fr.ubx.poo.ubgarden.game.launcher;


import fr.ubx.poo.ubgarden.game.go.decor.Land;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.*;

public class MapLevelDefaultStart extends MapLevel {


    private final static int width = 18;
    private final static int height = 8;
        private final MapEntity[][] level1 = {
                {Grass, Bombe, Grass, Grass, Grass, Carrots, Carrots, Carrots, Grass, Flowers, Grass, Grass, Grass, Grass, Bombe, Grass, Grass, DoorNextClosed},
                {Bombe, Gardener, Grass, Grass, Grass, Carrots, Carrots, Carrots, Grass, Apple, Grass, Grass, Grass, Grass, Apple, Grass, Grass, Grass},
                {Grass, Grass, Grass, Grass, Grass, Land, Land, Land, Grass, Grass, Flowers, Flowers, Grass, Grass, Grass, Grass, Grass, Grass},
                {Grass, Grass, Grass, Grass, Grass, Land, Land, Land, Grass, Grass, Grass, Grass, Grass, Tree, Flowers, NestHornet, Grass, Flowers},
                {PoisonedApple, Tree, Grass, Tree, Grass, Grass, Flowers, Flowers, Grass, Carrots, Carrots, Carrots, Grass, Tree, Flowers, Grass, Grass, Flowers},
                {Grass, Tree, Tree, Tree, PoisonedApple, Grass, Grass, Grass, Grass, Carrots, Carrots, Carrots, Hornets, Tree, Flowers, Flowers, Flowers, Flowers},
                {Grass, Grass, Grass, PoisonedApple, Grass, Grass, NestWasp, Grass, Grass, Carrots, Carrots, Carrots, Grass, Grass, Grass, Grass, Hedgehog, Bombe},
                {Apple, Tree, Apple, Tree, Grass, Grass, Wasps, Grass, Grass, Grass, Grass, Grass, Grass, Tree, Grass, Grass, Grass, Grass}
        };

    public MapLevelDefaultStart() {
        super(width, height);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                set(i, j, level1[j][i]);
    }


}
