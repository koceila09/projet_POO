/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Bombe_insecticide extends Bonus {

    public Bombe_insecticide(Position position, Decor decor) {
        super(position, decor);
    }


    @Override
    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez ramassé un insecticide !");
        gardener.setInsecticideNumber(gardener.getInsecticideNumber() + 1); // Incrémenter le compteur d'insecticides
        setDeleted(true); // Marquer comme supprimé
    }

}


















