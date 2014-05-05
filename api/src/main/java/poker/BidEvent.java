/*
 * @(#) BidEvent.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

/**
 * Object encapsulating information about a single bid.
 *
 * @version created  on 05/05/14, 15:43
 */
public class BidEvent extends BaseEvent {
    private final String player;
    private final MoveDTO move;

    public BidEvent(final String player, final MoveDTO move) {
        this.player = player;
        this.move = move;
    }

    @Override
    public String toString() {
        return player + " -> " + move;
    }

    public String getPlayer() {
        return player;
    }

    public MoveDTO getMove() {
        return move;
    }
}
