/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteWasp extends Sprite {

    public SpriteWasp(Pane layer, Wasps wasps) {
        super(layer, null, wasps);
        updateImage();
    }

    @Override
    public void updateImage() {
        Wasps wasps = (Wasps) getGameObject();
        Image image = getImage(wasps.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getWasp(direction);
    }
}
