/*
 * Copyright
 */
package poker;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 10:59
 */
public interface Player {
    final default String name() {
        return getClass().getName();
    }

    MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table);
}
