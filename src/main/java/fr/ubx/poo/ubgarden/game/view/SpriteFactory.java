/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.bonus.*;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import javafx.scene.layout.Pane;

import static fr.ubx.poo.ubgarden.game.view.ImageResource.*;


public final class SpriteFactory {

    public static Sprite create(Pane layer, GameObject gameObject) {
        ImageResourceFactory factory = ImageResourceFactory.getInstance();
        if (gameObject instanceof Grass)
            return new Sprite(layer, factory.get(GRASS), gameObject);
        if (gameObject instanceof Hedgehog)
            return new Sprite(layer, factory.get(HEDGEHOG), gameObject);
        if (gameObject instanceof Tree)
            return new Sprite(layer, factory.get(TREE), gameObject);
        if (gameObject instanceof Flowers)
            return new Sprite(layer, factory.get(FLOWERS), gameObject);
        if (gameObject instanceof NestWasp)
            return new Sprite(layer, factory.get(NESTWASP), gameObject);
        if (gameObject instanceof NestHornet)
            return new Sprite(layer, factory.get(NESTHORNET), gameObject);
        if (gameObject instanceof DoorNextClose)
            return new Sprite(layer, factory.get(DOOR_CLOSED), gameObject);
        if (gameObject instanceof DoorNextOpened)
            return new Sprite(layer, factory.get(DOOR_OPENED), gameObject);
        if (gameObject instanceof DoorPrevOpened)
            return new Sprite(layer, factory.get(DOOR_OPENED), gameObject);
        if (gameObject instanceof EnergyBoost)
            return new Sprite(layer, factory.get(APPLE), gameObject);
        if (gameObject instanceof Bombe_insecticide)
            return new Sprite(layer, factory.get(INSECTICIDE), gameObject);
        if (gameObject instanceof PoisonedApple)
            return new Sprite(layer, factory.get(POISONED_APPLE), gameObject);
        if (gameObject instanceof Carrots)
            return new Sprite(layer, factory.get(CARROTS), gameObject);
        if (gameObject instanceof Land)
            return new Sprite(layer, factory.get(LAND), gameObject);
        throw new RuntimeException("Unsupported sprite for decor " + gameObject);
    }
}