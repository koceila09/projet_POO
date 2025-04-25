/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Land;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class PoisonedApple extends Bonus {

    private static final int DISEASE_DURATION = 5000; // Durée de la maladie en millisecondes

    public PoisonedApple(Position position, Decor decor) {
        super(position, decor);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez mangé une pomme empoisonnée !");

        // Déterminer le multiplicateur en fonction du type de terrain
        Decor currentDecor = gardener.getCurrentDecor();
        int effectMultiplier = 1; // Par défaut, effet normal

        if (currentDecor instanceof Land) {
            effectMultiplier = 2; // Effet doublé sur la terre
        }

        // Appliquer l'effet de la pomme empoisonnée
        gardener.applyPoisonedEffect(effectMultiplier);

        setDeleted(true); // Supprimer la pomme empoisonnée après ramassage
    }
}

