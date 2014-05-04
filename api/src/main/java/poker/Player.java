/*
 * @(#) Player.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

/**
 * Core interface to be implemented by players (a minimum to be called 'player').
 *
 * @version created on 2014-04-17, 10:59
 */
public interface Player {
    default String name() {
        return getClass().getName();
    }

    MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table);
}
