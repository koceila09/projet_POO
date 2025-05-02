package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import javafx.scene.layout.Pane;

public class SpriteDoorOpened extends Sprite {
    public SpriteDoorOpened(Pane layer, Decor decor) {
        super(layer, ImageResourceFactory.getInstance().getDoorOpened(), decor);

    }
}