/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornets;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteHornet extends Sprite {

    public SpriteHornet(Pane layer, Hornets hornets) {
        super(layer, null, hornets);
        updateImage();
    }

    @Override
    public void updateImage() {
        Hornets hornets = (Hornets)  getGameObject();
        Image image = getImage(hornets.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getHornet(direction);
    }
}
