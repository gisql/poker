/*
 * Copyright
 */
package pl.gsiql.poker;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 12:21
 */
public interface DeathListener {
    /**
     * Notifies the player about his death.
     * <p/>
     * Players get killed by other players for making an illegal move (they cheat).  No mercy.
     * <p/>
     * Dead player doesn't receive any more notification.  He is dead.
     *
     * @param reason the reason for the killing.
     */
    void die(final String reason);
}
