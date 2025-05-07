/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.engine;

public class Timer {
    private final long duration;
    private long startTime;
    private boolean running = false;
    private boolean requested = false;
    private long remaining;

    // Set a timer for a duration in ms
    public Timer(long duration) {
        this.duration = duration;
        remaining = duration;
    }

    public void update(long now) {

        if (running) {
            remaining = duration * 1000000 - (now - startTime);
            if (remaining < 0) {
                running = false;
            }
        } else if (requested) {
            running = true;
            requested = false;
            startTime = now;
            remaining = duration;
        }
    }


    public void start() {
        if (!running)
            requested = true;
        else
            remaining = duration;
    }

    public boolean isRunning() {
        return running || requested;
    }
}
