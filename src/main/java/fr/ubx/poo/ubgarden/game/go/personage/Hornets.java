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
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class Hornets extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private Position position;
    private Direction direction;
    private boolean moveRequested = false;
    private int health = 2;

    public Hornets(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.position = position;

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
        // Vérifiez si la prochaine position est valide
        Position nextPos = direction.nextPosition(getPosition());
        return game.world().getGrid().inside(nextPos) && game.world().getGrid().get(nextPos) == null;
    }

    @Override
    public Position move(Direction direction) {
        if (canMove(direction)) {
            Position nextPos = direction.nextPosition(getPosition());
            setPosition(nextPos);
        }
        return getPosition();
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
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }

    // Méthode pour interagir avec le jardinier
    public void interactWith(Gardener gardener) {
        if (health > 0) {
            System.out.println("Le jardinier a été piqué par un frelon !");
            gardener.hurt(30); // Réduire l'énergie de 30 points
            health--; // Réduire la santé du frelon
        } else {
            System.out.println("Le frelon est mort !");
            setDeleted(true); // Supprimer le frelon après deux piqûres
        }
    }


    private void die() {
        setDeleted(true); // Supprimer le frelon du jeu
    }

}
