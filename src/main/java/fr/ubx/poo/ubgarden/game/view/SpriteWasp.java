package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteWasp extends Sprite {

    public SpriteWasp(Pane layer, Wasps wasp) {
        super(layer, null, wasp);
        updateImage(); // mettre la bonne image dès le début
    }

    @Override
    public void updateImage() {
        Wasps wasp = (Wasps) getGameObject();
        if (wasp.isDeleted()) {
            remove();
        } else {
            setImage(getImage(wasp.getDirection()));
        }
    }



    public Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getWasp(direction);
    }
}
