/*
 * @(#) BidListener.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

/**
 * Listener for bidding (or any other bid-like actions performed by players).  Note that only legal moves are
 * broadcasted.
 *
 * @version created on 2014-04-17, 11:05
 */
public interface BidListener {
    void moveMade(final String player, final MoveDTO move);
}
