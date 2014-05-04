/*
 * @(#) PokerHandTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poker.CardDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static poker.CardDTO.Suit.CLUBS;
import static poker.CardDTO.Suit.DIAMONDS;
import static poker.CardDTO.Suit.HEARTS;
import static poker.CardDTO.Suit.SPADES;
import static poker.CardDTO.Value.ACE;
import static poker.CardDTO.Value.FIVE;
import static poker.CardDTO.Value.FOUR;
import static poker.CardDTO.Value.KING;
import static poker.CardDTO.Value.SEVEN;
import static poker.CardDTO.Value.THREE;

public class PokerHandTest {
    public static final int MAX_TRIES = 10000;
    private final List<CardDTO> deck = Collections.unmodifiableList(CardDTO.deck());

    @Test(dataProvider = "seedsDP")
    public void testShouldSelect5Cards(final Long seed) throws Exception {
        final Random rnd = new Random(seed);
        for (int i = 0; i < MAX_TRIES; i++) {
            final List<CardDTO> cards = new LinkedList<>(deck);
            final List<CardDTO> com = deal(cards, rnd, 5);
            final List<CardDTO> own = deal(cards, rnd, 2);
            final PokerHand got = PokerHand.selectBest("test", com, own);
            Assert.assertEquals(got.getCards().size(), 5, "Hand " + i + " should have 5 cards");
        }
    }

    @Test(dataProvider = "seedsDP")
    public void testAllHandTypes(final Long seed) throws Exception {
        final Random rnd = new Random(seed);
        final Set<PokerHand.HandType> types = new HashSet<>();
        final List<CardDTO> cards = new LinkedList<>(deck);
        final List<PokerHand.HandType> expected = Arrays.asList(PokerHand.HandType.values());
        // for 5 cards, the royal flush is one per 649740. Let's give it 10 times more to be reasonably sure
        for (int j = 0; j < 7000000 / MAX_TRIES; j++) {
            for (int i = 0; i < MAX_TRIES; i++) {
                cards.clear();
                cards.addAll(deck);
                final List<CardDTO> com = deal(cards, rnd, 5);
                final List<CardDTO> own = deal(cards, rnd, 2);
                final PokerHand got = PokerHand.selectBest("test", com, own);
                types.add(got.getType());
            }
            if (types.size() == expected.size()) {
                break;
            }
        }

        final List<PokerHand.HandType> got = new ArrayList<>(types);
        Collections.sort(got);
        Assert.assertEquals(got, expected, "With enough trying, one should find all hand types");
    }

    @Test
    public void testRoyalFlush() throws Exception {
        final PokerHand hand = PokerHand.selectBest("test", deck.subList(8, 13), deck.subList(40, 42));
        Assert.assertEquals(hand.getType(), PokerHand.HandType.ROYAL_FLUSH);
    }

    @Test
    public void testSmallStraight() throws Exception {
        final PokerHand hand = PokerHand.selectBest("test", deck.subList(12, 17), deck.subList(40, 42));
        Assert.assertEquals(hand.getType(), PokerHand.HandType.STRAIGHT);
    }

    @Test
    public void testShouldNotWrap() throws Exception {
        final PokerHand hand = PokerHand.selectBest("test", deck.subList(10, 15), deck.subList(40, 42));
        Assert.assertNotEquals(hand.getType(), PokerHand.HandType.STRAIGHT);
    }

    @DataProvider
    public Object[][] seedsDP() {
        final Random rnd = new Random();
        final Object[][] rv = new Object[1][];
        for (int i = 0; i < rv.length; i++) {
            rv[i] = new Object[] {rnd.nextLong()};
        }
        return rv;
    }

    private List<CardDTO> deal(final List<CardDTO> cards, final Random rnd, final int n) {
        final List<CardDTO> rv = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            rv.add(cards.remove(rnd.nextInt(cards.size())));
        }
        return rv;
    }

    @Test
    public void testCompareToDiff() throws Exception {
        final List<CardDTO> nothing = Arrays.asList(new CardDTO(DIAMONDS, THREE), new CardDTO(DIAMONDS, FIVE), new CardDTO(DIAMONDS, SEVEN));
        final List<CardDTO> pair = Arrays.asList(new CardDTO(CLUBS, ACE), new CardDTO(HEARTS, ACE));
        final List<CardDTO> three = Arrays.asList(new CardDTO(CLUBS, KING), new CardDTO(HEARTS, KING), new CardDTO(SPADES, KING));

        final PokerHand pairHand = PokerHand.selectBest("pairHand", nothing, pair);
        final PokerHand threeHand = PokerHand.selectBest("threeHand", nothing, three);

        Assert.assertEquals(pairHand.getType(), PokerHand.HandType.ONE_PAIR);
        Assert.assertEquals(threeHand.getType(), PokerHand.HandType.THREE_OF_A_KIND);
        Assert.assertTrue(pairHand.compareTo(threeHand) < 0, "a THREE_OF_A_KIND should be greater than ONE_PAIR");
    }

    @Test
    public void testCompareToEq() throws Exception {
        final List<CardDTO> nothing1 = Arrays.asList(new CardDTO(DIAMONDS, THREE), new CardDTO(DIAMONDS, FIVE), new CardDTO(DIAMONDS, SEVEN));
        final List<CardDTO> nothing2 = Arrays.asList(new CardDTO(SPADES, THREE), new CardDTO(SPADES, FIVE), new CardDTO(SPADES, SEVEN));
        final List<CardDTO> pair = Arrays.asList(new CardDTO(CLUBS, ACE), new CardDTO(HEARTS, ACE));

        final PokerHand pair1 = PokerHand.selectBest("pairHand", nothing1, pair);
        final PokerHand pair2 = PokerHand.selectBest("pairHand", nothing2, pair);

        Assert.assertEquals(pair1.getType(), PokerHand.HandType.ONE_PAIR);
        Assert.assertEquals(pair2.getType(), PokerHand.HandType.ONE_PAIR);
        Assert.assertTrue(pair1.compareTo(pair2) == 0, "Both hands are equal");
    }

    @Test
    public void testCompareToKicker() throws Exception {
        final List<CardDTO> nothing1 = Arrays.asList(new CardDTO(DIAMONDS, THREE), new CardDTO(DIAMONDS, FIVE), new CardDTO(DIAMONDS, SEVEN));
        final List<CardDTO> nothing2 = Arrays.asList(new CardDTO(SPADES, FOUR), new CardDTO(SPADES, FIVE), new CardDTO(SPADES, SEVEN));
        final List<CardDTO> pair = Arrays.asList(new CardDTO(CLUBS, ACE), new CardDTO(HEARTS, ACE));

        final PokerHand pair1 = PokerHand.selectBest("pairHand", nothing1, pair);
        final PokerHand pair2 = PokerHand.selectBest("pairHand", nothing2, pair);

        Assert.assertEquals(pair1.getType(), PokerHand.HandType.ONE_PAIR);
        Assert.assertEquals(pair2.getType(), PokerHand.HandType.ONE_PAIR);
        Assert.assertTrue(pair1.compareTo(pair2) < 0, "Kicker in nothing 2 should decide");
    }
}
