package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.GameEngine;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.view.Sprite;
import fr.ubx.poo.ubgarden.game.view.SpriteFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Hornets extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private Direction direction;
    private boolean collisionHandled = false;
    private int health = 1;

    private final Timer moveTimer;
    private int steps = 0;

    public Hornets(Game game, Position position) {
        super(game, position);
        this.direction = Direction.random();
        this.moveTimer = new Timer( 1000/ game.configuration().hornetMoveFrequency() ); // fréquence de déplacement\
        this.moveTimer.start();
    }

    @Override
    public void update(long now) {

        collisionHandled = false;
        moveTimer.update(now);

        if (!moveTimer.isRunning()) {
            Direction[] directions = Direction.values();
            List<Direction> validDirections = new ArrayList<>();

            for (Direction dir : directions) {
                if (canMove(dir)) {
                    validDirections.add(dir);
                }
            }

            if (!validDirections.isEmpty()) {
                Random rand = new Random();
                direction = validDirections.get(rand.nextInt(validDirections.size()));
                move(direction);
                setModified(true);
                steps++;
                // 💣 Si on a atteint 5 pas, créer une bombe
                if (steps >= 5) {
                    steps = 0;

                    for (Direction dir : directions) {
                        Position bombPos = dir.nextPosition(getPosition());
                        if (game.world().getGrid().inside(bombPos)) {
                            Decor decor = game.world().getGrid().get(bombPos);

                            boolean isGrass = decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
                            boolean isEmpty = decor != null && decor.getBonus() == null;

                            if (isGrass && isEmpty) {
                                var bomb = new fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide(bombPos, decor);
                                decor.setBonus(bomb);
                                bomb.setModified(true);
                                Sprite sprite = SpriteFactory.create(game.getGameEngine().getLayer(), bomb);
                                game.getGameEngine().addSprite(sprite);
                                System.out.println("💣 frelons a posé une bombe à " + bombPos);
                                break; // seulement une bombe
                            }
                        }
                    }
                }
            }

            // 💥 Vérifier si la guêpe marche sur une bombe
            Decor decor = game.world().getGrid().get(getPosition());
            if (decor != null && decor.getBonus() instanceof fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide bomb) {
                System.out.println("💥 frelons touchée par une bombe et morte !");
                setDeleted(true);       // Supprime la guêpe
                bomb.setDeleted(true);  // Supprime le sprite visuel de la bombe
                decor.setBonus(null);   // Retire la bombe de la grille
                return;                 // Stoppe tout
            }

            moveTimer.start();
        }
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        if (!game.world().getGrid().inside(nextPos))
            return false;
        Decor decor = game.world().getGrid().get(nextPos);
        return (decor == null || decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass);
    }

    @Override
    public Position move(Direction direction) {
        if (canMove(direction)) {
            Position nextPos = direction.nextPosition(getPosition());
            setPosition(nextPos);
        }
        return getPosition();
    }

    public Direction getDirection() {
        return direction;
    }

    public void interactWith(Gardener gardener) {
        if (!collisionHandled && !isDeleted()) {
            if (gardener.getInsecticideNumber() >= 2) {
                gardener.setInsecticideNumber(gardener.getInsecticideNumber() - 2);
                System.out.println("💣 Le jardinier utilise 2 bombes et tue le frelon !");
                setDeleted(true);
            } else {
                System.out.println("Le jardinier a été piqué par un frelon !");
                gardener.hurt(30);
                setDeleted(true); // Le frelon meurt aussi après la piqûre
            }
            collisionHandled = true;
        }
    }

}