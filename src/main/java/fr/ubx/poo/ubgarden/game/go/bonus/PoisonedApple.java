/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class PoisonedApple extends Bonus {

    private static final int DISEASE_DURATION = 5000; // Durée de la maladie en millisecondes

    public PoisonedApple(Position position, Decor decor) {
        super(position, decor);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez mangé une pomme empoisonnée ! Vous êtes malade.");
        gardener.increaseDiseaseLevel(DISEASE_DURATION); // Augmenter le niveau de fatigue
        setDeleted(true); // Supprimer la pomme empoisonnée après ramassage
    }
}

