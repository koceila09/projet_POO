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
import java.util.List;
import java.util.Random;

public class Wasps extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private Direction direction;
    private int health = 1;
    private boolean collisionHandled = false;

    private final Timer moveTimer;
    private int steps = 0;
    private final int maxSteps = 15;

    public Wasps(Game game, Position position) {
        super(game, position);
        this.direction = Direction.random();
        this.moveTimer = new Timer(game.configuration().waspMoveFrequency() * 1000); // ✅ basé sur config
        // 1 pas par seconde
    }

    @Override
    public void update(long now) {
        collisionHandled = false;
        moveTimer.update(now);

        if (!moveTimer.isRunning()) {
            // Choisir une direction aléatoire valide
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

                // ➕ Incrémenter le compteur de pas
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
                                System.out.println("💣 Guêpe a posé une bombe à " + bombPos);
                                break; // seulement une bombe
                            }
                        }
                    }
                }
            }
            // 💥 Vérifier si la guêpe marche sur une bombe
            Decor decor = game.world().getGrid().get(getPosition());
            if (decor != null && decor.getBonus() instanceof fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide bomb) {
                System.out.println("💥 Guêpe touchée par une bombe et morte !");
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
            // pas besoin de setModified ici si pas de changement de direction
        }
        return getPosition();

    }

    public void interactWith(Gardener gardener) {
        if (!collisionHandled && !isDeleted()) {
            System.out.println("Le jardinier a été piqué par une guêpe !");
            gardener.hurt(20);
            health--;
            collisionHandled = true;
            if (health <= 0) {
                System.out.println("La guêpe est morte !");
                setDeleted(true);
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    private void die() {
        setDeleted(false);
    }
    private GameEngine engine;

    public void setGameEngine(GameEngine engine) {
        this.engine = engine;
    }

    public GameEngine getGameEngine() {
        return engine;
    }

}
