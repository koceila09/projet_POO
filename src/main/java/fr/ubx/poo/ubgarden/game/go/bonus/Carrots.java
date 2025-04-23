package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Carrots extends Bonus {

    public Carrots(Position position, Decor decor) {
        super(position, decor);
    }

    @Override
    public void remove() {
        super.remove();
    }



    @Override
    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez ramassé une pomme ! Votre énergie augmente.");

        setDeleted(true); // Supprimer la pomme après ramassage
    }

}