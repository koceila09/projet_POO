package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorNextClose extends Decor {
    public DoorNextClose(Position position) {
        super(position);
    }

    @Override
    public int energyConsumptionWalk() {
        return super.energyConsumptionWalk();
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public int getMoveCost() {
        return 0;
    }
}
