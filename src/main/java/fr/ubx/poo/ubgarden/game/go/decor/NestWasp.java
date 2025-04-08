package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class NestWasp extends Decor {
    public NestWasp(Position position) {
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

/*
 * Copyright (c) 2020. Laurent Réveillère
 */
