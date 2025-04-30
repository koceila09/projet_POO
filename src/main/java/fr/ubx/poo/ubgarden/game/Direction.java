/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game;

import java.util.Random;

public enum Direction {
    UP {
        @Override
        public Position nextPosition(Position pos, int delta) {
            return new Position(pos.level(), pos.x(), pos.y() - delta);
        }
    },
    RIGHT {
        @Override
        public Position nextPosition(Position pos, int delta) {
            return new Position(pos.level(), pos.x() + delta, pos.y());
        }
    },
    DOWN {
        @Override
        public Position nextPosition(Position pos, int delta) {
            return new Position(pos.level(), pos.x(), pos.y() + delta);
        }
    },
    LEFT {
        @Override
        public Position nextPosition(Position pos, int delta) {
            return new Position(pos.level(), pos.x() - delta, pos.y());
        }
    },
    ;

    private static final Random randomGenerator = new Random();



    public abstract Position nextPosition(Position pos, int delta);

    public Position nextPosition(Position pos) {
        return nextPosition(pos, 1);
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public static Direction random() {
        Direction[] values = values();
        return values[(int)(Math.random() * values.length)];
    }


}