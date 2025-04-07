/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;

import javafx.scene.paint.Color;



public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private Direction direction;
    private boolean moveRequested = false;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
// TODO
        System.out.println("I am taking the boost, I should do something ...");

    }


    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        // Vérifie si la position est dans les limites de la carte
        if (!game.world().getGrid().inside(nextPos)) {
            return false;
        }

        // Récupère l'objet décor à la position suivante
        Decor nextDecor = game.world().getGrid().get(nextPos);

        // Si aucun décor, alors on peut avancer
        if (nextDecor == null) {
            return true;
        }

        // Sinon, on vérifie si ce décor est franchissable
        return nextDecor.walkableBy(this);
    }


    @Override


    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);

        // Vérifier si le jardinier a trouvé le hérisson
        if (next instanceof Hedgehog) {
            System.out.println("Game Won ! Vous avez retrouvé le hérisson !");
            game.endGame(true); // Terminer la partie en cas de victoire
            return nextPos;
        }

        // Interaction avec les bonus
        if (next != null) {
            next.pickUpBy(this);
        }

        return nextPos;
    }
    public boolean hasFoundHedgehog() {
        // Récupérer l'entité à la position actuelle du jardinier
        Decor decorAtCurrentPosition = game.world().getGrid().get(getPosition());
        return decorAtCurrentPosition instanceof Hedgehog; // Vérifier si l'entité est un hérisson
    }

    public void update(long now) {

        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
            }
        }
        moveRequested = false;
    }

    public void hurt(int damage) {
        this.energy -= damage;
        if (this.energy <= 0) {
            System.out.println("Le jardinier est mort ! Game Over.");
            game.endGame(false); // Terminer la partie en indiquant la défaite
        } else {
            System.out.println("Vous avez perdu " + damage + " points d'énergie. Énergie restante : " + energy);
        }
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }



}
