/*
 * Copyright
 */
package poker;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 14:40
 */
public class CardDTO {
    private final Suit suit;
    private final Value value;

    public CardDTO(final Suit suit, final Value value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    public static enum Suit {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS
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
        KNAVE(11),
        QUEEN(12),
        KING(13),
        ACE(14);
        private int value;

        Value(final int value) {
            this.value = value;
        }
    }
}
