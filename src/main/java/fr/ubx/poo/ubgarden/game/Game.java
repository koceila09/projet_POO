package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornets;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;
import java.util.stream.Collectors;
import fr.ubx.poo.ubgarden.game.Position;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private final List<Wasps> wasps; // Liste de guêpes
    private final List<Hornets> hornets;
    private final List<Position> waspPositions;
    private final List<Position> hornetPositions;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    private int carrotCount = 0;



    public Game(World world, Configuration configuration, Position gardenerPosition,
                List<Position> waspPositions, List<Position> hornetPositions) {
        this.configuration = configuration;
        this.world = world;
        this.gardener = new Gardener(this, gardenerPosition);
        this.waspPositions = waspPositions; // Initialisation correcte
        this.hornetPositions = hornetPositions; // Initialisation correcte

        // Initialiser les listes de guêpes et de frelons
        this.wasps = new ArrayList<>(waspPositions.stream()
                .map(pos -> new Wasps(this, pos))
                .toList());

        this.hornets = new ArrayList<>(hornetPositions.stream()
                .map(pos -> new Hornets(this, pos))
                .toList());

    }

    private boolean gameOver = false;
    private boolean gameWon = false;

    public void endGame(boolean won) {
        this.gameOver = true;
        this.gameWon = won;
        System.out.println(won ? "Game Won!" : "Game Over");
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }








    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public List<Wasps> getWasps() {
        return wasps; // Renvoie la liste des guêpes
    }

    public List<Hornets> getHornets() {
        return hornets; // Renvoie la liste des frelons
    }
    public List<Position> getWaspPositions() {
        return waspPositions; // Renvoie les positions des guêpes
    }

    public List<Position> getHornetPositions() {
        return hornetPositions; // Renvoie les positions des frelons
    }





    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

    public void checkGameState(Gardener gardener) {
        if (gardener.hasFoundHedgehog()) {
            endGame(true); // Victoire
        } else if (gardener.getEnergy() <= 0) {
            endGame(false); // Défaite
        }
    }


    // Autres attributs et méthodes...

    public void setCarrotCount(int count) {
        this.carrotCount = count;
    }

    public void collectCarrot() {
        carrotCount--;
    }

    public boolean allCarrotsCollected() {
        return carrotCount <= 0;
    }


    private boolean doorsOpened = false;

    public boolean areDoorsOpened() {
        return doorsOpened;
    }

    public void setDoorsOpened(boolean opened) {
        this.doorsOpened = opened;
    }


}
