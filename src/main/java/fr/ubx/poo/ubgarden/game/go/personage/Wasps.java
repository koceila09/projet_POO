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

public class Wasps extends GameObject implements Movable, PickupVisitor, WalkVisitor {


    private Direction direction;
    private boolean moveRequested = false;

    public Wasps(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;

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
        // TO UPDATE
        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        //if (next != null)
            //next.pickUpBy(this);
        return nextPos;
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


}
