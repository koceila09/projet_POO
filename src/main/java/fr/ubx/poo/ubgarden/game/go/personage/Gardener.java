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
import fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.decor.Land;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;

import javafx.scene.paint.Color;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.PoisonedApple;
import fr.ubx.poo.ubgarden.game.engine.Timer;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private int maxEnergy;
    private Direction direction;
    private boolean moveRequested = false;
    private int diseaseLevel = 0;
    private int insecticideNumber = 0;
    private long poisonedEffectStartTime; // Stocke le moment où l'effet de la pomme commence
    private int poisonedEffectDuration = 5000; // Durée de l'effet en millisecondes (5 secondes)
    private int energyDrainPerSecond = 10; // Quantité d'énergie drainée par seconde (par défaut)
    private long diseaseStartTime; // Stocke le moment où la maladie commence
    private int diseaseDuration = 5000;
    private long lastMoveTime;
    private final Timer restTimer = new Timer(1000); // 1 seconde = 1000 ms


    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.maxEnergy = game.configuration().gardenerEnergy(); // Énergie maximale initiale
        this.energy = maxEnergy;
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        System.out.println("Vous avez ramassé un bonus d'énergie !");
        setEnergy(getEnergy() + energyBoost.getEnergyBoost()); // Augmenter l'énergie
        energyBoost.setDeleted(true); // Supprimer le bonus après ramassage
        Decor decor = game.world().getGrid().get(getPosition());
        if (decor != null && decor.getBonus() == energyBoost) {
            decor.setBonus(null); // Supprimer définitivement la pomme
        }

    }
    public void pickUp(Bombe_insecticide bomb) {
        System.out.println("Vous avez ramassé une bombe insecticide !");
        setInsecticideNumber(getInsecticideNumber() + 1);
        bomb.setDeleted(true);

        // Supprimer définitivement de la grille du monde
        Decor decor = game.world().getGrid().get(getPosition());
        if (decor != null && decor.getBonus() == bomb) {
            decor.setBonus(null);
        }
    }





    public int getEnergy() {
        return this.energy;
    }
    public int getMaxEnergy() {
        return maxEnergy;
    }
    public void setEnergy(int energy) {
        this.energy = Math.min(energy, maxEnergy); // Limiter l'énergie au maximum autorisé
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

        // Calculer le coût de déplacement
        if (next != null) {
            if (next instanceof Land) {
                hurt(2);
            }
            else {
                hurt(1);
            }
        }

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
        // Gérer le déplacement s’il a été demandé
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
            }
            restTimer.start(); // ← ici aussi, si le joueur s'est déplacé
        }

        moveRequested = false;

        updateDiseaseLevel(); // effet des pommes pourries

        // Met à jour le timer d’énergie
        restTimer.update(now);

        // Si le joueur est resté immobile assez longtemps, il regagne de l’énergie
        if (!restTimer.isRunning() && energy < maxEnergy) {
            energy++;
            restTimer.start(); // On relance le timer pour attendre la prochaine régénération
        }
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



    public Direction getDirection() {
        return direction;
    }



    public int getDiseaseLevel() {
        return diseaseLevel; // Retourne le niveau de maladie du jardinier
    }

    public int getInsecticideNumber() {
        return insecticideNumber;
    }

    public void setInsecticideNumber(int number) {
        this.insecticideNumber = number;
    }

    public void updateDiseaseLevel() {
        if (diseaseLevel > 0) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - diseaseStartTime;

            // Vérifier si la durée de la maladie est écoulée
            if (elapsedTime >= diseaseDuration) {
                this.diseaseLevel -= 1; // Réduire le niveau de fatigue
                this.diseaseStartTime = currentTime; // Réinitialiser le temps de début
                System.out.println("Votre niveau de fatigue diminue ! Niveau restant : " + diseaseLevel);
            }
        }
    }

    public Decor getCurrentDecor() {
        return game.world().getGrid().get(getPosition());
    }




    public void pickUpBy(Gardener gardener) {
        System.out.println("Vous avez mangé une pomme empoisonnée ! Vous êtes malade.");

        // Déterminer le multiplicateur en fonction du type de terrain
        Decor currentDecor = gardener.getCurrentDecor();
        int effectMultiplier = 1; // Par défaut, effet normal

        if (currentDecor instanceof Land) {
            effectMultiplier = 2; // Effet doublé sur la terre
        }

        // Appliquer l'effet de maladie avec le multiplicateur
        gardener.increaseDiseaseLevel(effectMultiplier);

        setDeleted(true); // Supprimer la pomme empoisonnée après ramassage

    }

    public void increaseDiseaseLevel(int effectMultiplier) {
        // Réduire l'effet de la maladie en fonction du nombre de bombes insecticides
        int adjustedEffect = effectMultiplier / (insecticideNumber + 1); // Divisé par (nombre de bombes + 1)

        this.diseaseLevel += adjustedEffect; // Augmenter le niveau de fatigue
        this.diseaseStartTime = System.currentTimeMillis(); // Enregistrer le moment où la maladie commence
        System.out.println("Votre niveau de fatigue augmente ! Nouveau niveau : " + diseaseLevel);
    }

    public void applyPoisonedEffect(int effectMultiplier) {
        this.poisonedEffectStartTime = System.currentTimeMillis(); // Enregistrer le moment où l'effet commence

        // Calculer l'impact sur l'énergie en fonction du multiplicateur et du nombre de bombes insecticides
        int adjustedEnergyDrain = (energyDrainPerSecond * effectMultiplier) / (insecticideNumber + 1);

        System.out.println("Vous êtes affecté par une pomme empoisonnée ! Perte d'énergie : " + adjustedEnergyDrain + " par seconde.");
    }

    public void updatePoisonedEffect() {
        if (System.currentTimeMillis() - poisonedEffectStartTime < poisonedEffectDuration) {
            // Calculer l'impact sur l'énergie en fonction du multiplicateur et du nombre de bombes insecticides
            Decor currentDecor = getCurrentDecor();
            int effectMultiplier = 1; // Par défaut, effet normal

            if (currentDecor instanceof Land) {
                effectMultiplier = 2; // Effet doublé sur la terre
            }

            int adjustedEnergyDrain = (energyDrainPerSecond * effectMultiplier) / (insecticideNumber + 1);
            hurt(adjustedEnergyDrain); // Réduire l'énergie du Gardener
        } else {
            System.out.println("L'effet de la pomme empoisonnée a expiré.");
        }
    }



    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        // Si le joueur ne bouge pas (mur, etc.), on ne fait rien :
        if (nextPos.equals(getPosition())) return;

        // Sinon, on bouge :
        setPosition(nextPos);
        restTimer.start();

        // Ici on met à jour le temps du dernier mouvement :
        lastMoveTime = System.currentTimeMillis();

        // Et on peut aussi retirer de l’énergie ici si besoin
    }







}
