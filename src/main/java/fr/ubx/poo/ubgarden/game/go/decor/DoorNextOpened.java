package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorNextOpened extends Decor {
    public DoorNextOpened(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public int energyConsumptionWalk() {
        return super.energyConsumptionWalk();
    }

    @Override
    public int getMoveCost() {
        return 0;
    }
}
