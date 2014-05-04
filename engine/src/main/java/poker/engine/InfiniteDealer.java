/*
 * @(#) InfiniteDealer.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;

import java.security.SecureRandom;
import java.util.List;

/**
 * Implements a dealer dealing from a deck with infinite number of cards.
 *
 * @version created on 26/04/14, 10:08
 */
class InfiniteDealer implements Dealer {
    private final static List<CardDTO> DECK = CardDTO.deck();
    private final SecureRandom rnd;

    public InfiniteDealer(byte[] seed) {
        rnd = new SecureRandom(seed);
    }

    public InfiniteDealer() {
        rnd = new SecureRandom();
    }

    @Override
    public void burn() {
        // do nothing
    }

    @Override
    public void shuffle() {
        // do nothing
    }

    @Override
    public CardDTO deal() {
        return DECK.get(rnd.nextInt(DECK.size()));
    }
}
