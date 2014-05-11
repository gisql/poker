/*
 * @(#) PokerHand.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;
import poker.tools.HandEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A representation of a Poker 'hand' (5 cards).
 *
 * @version created on 04/05/14, 14:18
 */
class PokerHand implements Comparable<PokerHand> {
    private static final Map<HandType, FCC> EVALUATORS = new LinkedHashMap<HandType, FCC>() {{
        put(HandType.ROYAL_FLUSH, HandEvaluator::royalFlush);
        put(HandType.ROYAL_FLUSH, HandEvaluator::royalFlush);
        put(HandType.STRAIGHT_FLUSH, HandEvaluator::straightFlush);
        put(HandType.FOUR_OF_A_KIND, HandEvaluator::fourOfAKind);
        put(HandType.FULL_HOUSE, HandEvaluator::fullHouse);
        put(HandType.FLUSH, HandEvaluator::flush);
        put(HandType.STRAIGHT, HandEvaluator::straight);
        put(HandType.THREE_OF_A_KIND, HandEvaluator::threeOfAKind);
        put(HandType.TWO_PAIRS, HandEvaluator::twoPairs);
        put(HandType.ONE_PAIR, HandEvaluator::onePair);
    }};

    private final String owner;
    private final HandType type;

    private final List<CardDTO> cards;

    private PokerHand(final String owner, final List<CardDTO> cards, final HandType type) {
        this.owner = owner;
        this.type = type;
        this.cards = Collections.unmodifiableList(cards);
    }

    public static PokerHand selectBest(final String owner, final List<CardDTO> com, final List<CardDTO> own) {
        final List<CardDTO> all = join(com, own);

        for (final Map.Entry<HandType, FCC> entry : EVALUATORS.entrySet()) {
            final List<CardDTO> cards = entry.getValue().apply(all);
            if (cards != null) {
                return new PokerHand(owner, cards, entry.getKey());
            }
        }

        return new PokerHand(owner, HandEvaluator.topN(all, 5), HandType.HIGH_CARD);
    }

    private static List<CardDTO> join(final List<CardDTO> com, final List<CardDTO> own) {
        final List<CardDTO> all = new ArrayList<>(com.size() + own.size());
        all.addAll(com);
        all.addAll(own);
        return Collections.unmodifiableList(all);
    }

    @Override
    public String toString() {
        return CardDTO.toString(cards) + ": " + type.name().replace("_", " ");
    }

    @Override
    public int compareTo(final PokerHand o) {
        final int typeCmp = -type.compareTo(o.type);
        if (typeCmp != 0) {
            return typeCmp;
        }
        for (int i = 0; i < 5; i++) {
            final int cmp = cards.get(i).getValue().compareTo(o.cards.get(i).getValue());
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    public String getOwner() {
        return owner;
    }

    public HandType getType() {
        return type;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public static enum HandType {
        ROYAL_FLUSH,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIRS,
        ONE_PAIR,
        HIGH_CARD
    }

    private static interface FCC extends Function<List<CardDTO>, List<CardDTO>> {
    }
}
