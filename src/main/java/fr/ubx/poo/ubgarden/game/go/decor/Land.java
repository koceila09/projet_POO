/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Land extends Decor {
    public Land(Position position) {
        super(position);
    }

    public Land(Position position, Bonus bonus) {
        super(position, bonus);
    }
}

