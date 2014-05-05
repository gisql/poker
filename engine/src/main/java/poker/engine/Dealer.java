/*
 * @(#) Dealer.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;

/**
 * Dealer abstraction
 *
 * @version created on 26/04/14, 10:03
 */
interface Dealer {
    /**
     * Burns a card from the top of the dealing deck.
     * <p/>
     * Note, this method might re-initialise the dealing deck.
     */
    void burn();

    /**
     * Shuffles the dealing deck
     */
    void shuffle();

    /**
     * Deals next card from the dealing deck.
     * <p/>
     * Note, this method might re-initialise the dealing deck.
     *
     * @return the next card
     */
    CardDTO deal();
}
