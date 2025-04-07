package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornets;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private final Wasps wasps;
    private final Hornets hornets;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    public Game(World world, Configuration configuration, Position gardenerPosition, Position waspposition, Position hornetposition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
        wasps = new Wasps(this,waspposition);
        hornets = new Hornets(this,hornetposition);

    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }
    public Wasps getWasps() { return this.wasps;}
    public Hornets getHornets() { return this.hornets;}
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

}
