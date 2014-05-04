/*
 * @(#) CardDTOTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

public class CardDTOTest {
    @Test
    public void testDeck() throws Exception {
        final List<CardDTO> deck = CardDTO.deck();
        Assert.assertEquals(deck.size(), 4 * 13, "Deck should have 52 cards.");

        for (int i = 0; i < deck.size() - 1; i++) {
            for (int j = i + 1; j < deck.size(); j++) {
                final CardDTO cardI = deck.get(i);
                final CardDTO cardJ = deck.get(j);
                Assert.assertFalse(cardI.getSuit() == cardJ.getSuit() && cardI.getValue() == cardJ.getValue(),
                        "Cards in deck should be unique; found: " + cardI + " and " + cardJ);
            }
        }
    }

    @Test
    public void testRandomAccess() throws Exception {
        Assert.assertTrue(CardDTO.deck() instanceof RandomAccess, "Decks should implement random access interface");
    }

    @Test
    public void testEqContract() throws Exception {
        final Set<CardDTO> deck = new HashSet<>(CardDTO.deck());
        deck.addAll(CardDTO.deck());
        Assert.assertEquals(deck.size(), CardDTO.deck().size(), "equals/hashCode should 'detect' duplications");
    }

    @Test
    public void testNewDeckSorted() throws Exception {
        final List<CardDTO> deck = CardDTO.deck();
        final List<CardDTO> sorted = new ArrayList<>(deck);
        Collections.sort(sorted);
        Assert.assertEquals(deck, sorted, "new decks should be sorted (and sorting shouldn't change the order)");
    }

    @Test
    public void testSort() throws Exception {
        final List<CardDTO> deck = CardDTO.deck();
        final List<CardDTO> sorted = new ArrayList<>(deck);
        Collections.shuffle(sorted);
        Assert.assertNotEquals(deck, sorted, "it is very unlikely that the shuffled deck comes out as a sorted one");
        Collections.sort(sorted);
        Assert.assertEquals(deck, sorted, "new decks are assumed to be sorted, the sorted should be equal to the new deck");
    }

    @Test
    public void testValueOfValue() throws Exception {
        final List<CardDTO> deck = CardDTO.deck();
        for (int i = 0; i < 12; i++) {
            final CardDTO cardI = deck.get(i);
            final CardDTO cardJ = deck.get(i + 1);
            Assert.assertEquals(cardI.getSuit(), cardJ.getSuit(), "Deck should start with 13 clubs");
            Assert.assertEquals(cardI.getSuit(), CardDTO.Suit.CLUBS, "Deck should start with 13 clubs");
            Assert.assertEquals(cardJ.getValue().value - cardI.getValue().value, 1, "Difference in value between two neighbouring cards in sorted deck should be 1");
        }
    }
}
