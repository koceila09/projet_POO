/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorNextClosed extends Decor {
    public DoorNextClosed(Position position) {
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

