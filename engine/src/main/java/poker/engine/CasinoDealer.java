/*
 * @(#) CasinoDealer.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Implements the 'standard' dealer returning next cards from the deck.
 *
 * @version created on 26/04/14, 10:08
 */
class CasinoDealer implements Dealer {
    private final int numberOfDecks;
    private final int minNumberOfCards;

    private final Stack<CardDTO> stack;

    public CasinoDealer(final int numberOfDecks, final int minNumberOfCards) {
        this.numberOfDecks = numberOfDecks;
        this.minNumberOfCards = minNumberOfCards;
        stack = new Stack<>();
        shuffle();
    }

    public CasinoDealer(final int numberOfDecks) {
        this(numberOfDecks, 1);
    }

    @Override
    public synchronized void burn() {
        deal();
    }

    @Override
    public synchronized void shuffle() {
        stack.removeAllElements();
        final List<CardDTO> deck = CardDTO.deck();
        for (int i = 0; i < numberOfDecks; i++) {
            stack.addAll(deck);
        }
        Collections.shuffle(stack);
    }

    @Override
    public synchronized CardDTO deal() {
        if (stack.size() < minNumberOfCards) {
            shuffle();
        }
        return stack.pop();
    }
}
