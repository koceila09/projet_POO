/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class EnergyBoost extends Bonus {

    private static final int ENERGY_BOOST = 50;

    public EnergyBoost(Position position, Decor decor) {
        super(position, decor);
    }

    public int getEnergyBoost() {
        return ENERGY_BOOST; // Retourne la quantité d'énergie
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez ramassé une pomme ! Votre énergie augmente.");
        gardener.setEnergy(Math.min(gardener.getEnergy() + ENERGY_BOOST, gardener.getMaxEnergy()));
        setDeleted(true); // Supprimer la pomme après ramassage
    }


}
