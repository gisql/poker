/*
 * @(#) CardDTO.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single standard playing card from the 52-card deck.
 *
 * @version created on 2014-04-17, 14:40
 */
public final class CardDTO implements Comparable<CardDTO> {
    private static final List<CardDTO> DECK;

    private final Suit suit;
    private final Value value;

    static {
        DECK = new ArrayList<>(52);
        for (final Suit s : Suit.values()) {
            for (final Value v : Value.values()) {
                DECK.add(new CardDTO(s, v));
            }
        }
    }

    public CardDTO(final Suit suit, final Value value) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public int compareTo(final CardDTO o) {
        return -suitFirstDesc(this, o);
    }

    public static int suitFirstDesc(final CardDTO c1, final CardDTO c2) {
        final int suitCmp = Integer.compare(c1.suit.ordinal(), c2.suit.ordinal());
        if (suitCmp == 0) {
            return -Integer.compare(c1.value.ordinal(), c2.value.ordinal());
        }

        return -suitCmp;
    }

    public static int valueFirstDesc(final CardDTO c1, final CardDTO c2) {
        final int valueCmp = Integer.compare(c1.value.ordinal(), c2.value.ordinal());
        if (valueCmp == 0) {
            return -Integer.compare(c1.suit.ordinal(), c2.suit.ordinal());
        }

        return -valueCmp;
    }

    public static int valueFirstOneDesc(final CardDTO c1, final CardDTO c2) {
        final int v1 = c1.value == Value.ACE? 1: c1.value.value;
        final int v2 = c2.value == Value.ACE? 1: c2.value.value;
        final int valueCmp = Integer.compare(v1, v2);
        if (valueCmp == 0) {
            return -Integer.compare(c1.suit.ordinal(), c2.suit.ordinal());
        }

        return -valueCmp;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardDTO)) {
            return false;
        }

        final CardDTO cardDTO = (CardDTO)o;
        return suit == cardDTO.suit && value == cardDTO.value;
    }

    @Override
    public final int hashCode() {
        int result = suit.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        // 2009 is 'thin space'
        return value + "\u2009" + suit;
    }

    /**
     * Renders a string representation of a list of cards
     */
    public static String toString(final List<CardDTO> cards) {
        return Arrays.toString(cards.toArray(new CardDTO[cards.size()]));
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    /**
     * @return a full deck (with random access) of cards in a natural order
     */
    public static List<CardDTO> deck() {
        return new ArrayList<>(DECK);
    }

    public static enum Suit {
        CLUBS,
        DIAMONDS,
        HEARTS,
        SPADES;
        private static Map<Suit, String> toStrings = new HashMap<Suit, String>() {{
            put(CLUBS, "♣");
            put(DIAMONDS, "♦");
            put(HEARTS, "♥");
            put(SPADES, "♠");
        }};

        @Override
        public String toString() {
            return toStrings.get(this);
        }
    }

    public static enum Value {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        public int value;

        Value(final int value) {
            this.value = value;
        }

        public boolean isNext(final Value other) {
            return this.ordinal() == other.ordinal() + 1
                    || this == TWO && other == ACE;
        }

        @Override
        public String toString() {
            if (value < 11) {
                return Integer.toString(value);
            } else {
                return "" + name().charAt(0);
            }
        }
    }
}
