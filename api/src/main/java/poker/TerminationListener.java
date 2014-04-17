/*
 * Copyright
 */
package poker;

public interface TerminationListener {
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

    /**
     * Notifies the player that he lost.
     * <p/>
     * Players who lost still can listen to all events.  However, they'll never be asked to make a move.
     */
    void lost();
}
