package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Pickupable;
import fr.ubx.poo.ubgarden.game.go.Walkable;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public abstract class Decor extends GameObject implements Walkable, Pickupable {

    private Bonus bonus;

    public Decor(Position position) {
        super(position);
    }

    public Decor(Position position, Bonus bonus) {
        super(position);
        this.bonus = bonus;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
        this.setModified(true);
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public void update(long now) {
        super.update(now);
        if (bonus != null) bonus.update(now);
    }
    public void pickUpBy(Gardener gardener) {
        if (bonus != null) {
            bonus.pickUpBy(gardener);
            setBonus(null); // Supprimer le bonus après ramassage
        }
    }

    public void clearBonus() {
        this.bonus = null;
        setModified(true); // pour redessiner la case sans bonus
    }

    public abstract int getMoveCost();

}