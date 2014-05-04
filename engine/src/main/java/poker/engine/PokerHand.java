/*
 * @(#) PokerHand.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * A representation of a Poker 'hand' (5 cards).
 *
 * @version created on 04/05/14, 14:18
 */
public class PokerHand implements Comparable<PokerHand> {
    public static final BiP FALSE = (a, b) -> false;

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

        List<CardDTO> cards;
        if ((cards = royalFlush(all)) != null) {
            return new PokerHand(owner, cards, HandType.ROYAL_FLUSH);
        } else if ((cards = straightFlush(all)) != null) {
            return new PokerHand(owner, cards, HandType.STRAIGHT_FLUSH);
        } else if ((cards = fourOfAKind(all)) != null) {
            return new PokerHand(owner, cards, HandType.FOUR_OF_A_KIND);
        } else if ((cards = fullHouse(all)) != null) {
            return new PokerHand(owner, cards, HandType.FULL_HOUSE);
        } else if ((cards = flush(all)) != null) {
            return new PokerHand(owner, cards, HandType.FLUSH);
        } else if ((cards = straight(all)) != null) {
            return new PokerHand(owner, cards, HandType.STRAIGHT);
        } else if ((cards = threeOfAKind(all)) != null) {
            return new PokerHand(owner, cards, HandType.THREE_OF_A_KIND);
        } else if ((cards = twoPairs(all)) != null) {
            return new PokerHand(owner, cards, HandType.TWO_PAIRS);
        } else if ((cards = onePair(all)) != null) {
            return new PokerHand(owner, cards, HandType.ONE_PAIR);
        } else {
            return new PokerHand(owner, topN(all, 5), HandType.HIGH_CARD);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(cards.toArray(new CardDTO[cards.size()])) + ": " + type.name().replace("_", " ");
    }

    private static List<CardDTO> groups(final List<CardDTO> in, int... sizes) {
        final List<CardDTO> all = valueSort(in);
        final List<CardDTO> rv = new ArrayList<>(5);
        for (int size : sizes) {
            final List<CardDTO> group = removeGroup(all, size);
            if (group == null) {
                return null;
            }
            rv.addAll(group);
        }
        if (rv.size() < 5) {
            rv.addAll(topN(all, 5 - rv.size()));
        }
        return rv;
    }

    private static List<CardDTO> onePair(final List<CardDTO> in) {
        return groups(in, 2);
    }

    private static List<CardDTO> twoPairs(final List<CardDTO> in) {
        return groups(in, 2, 2);
    }

    private static List<CardDTO> threeOfAKind(final List<CardDTO> in) {
        return groups(in, 3);
    }

    private static List<CardDTO> fullHouse(final List<CardDTO> in) {
        return groups(in, 3, 2);
    }

    private static List<CardDTO> fourOfAKind(final List<CardDTO> in) {
        return groups(in, 4);
    }

    private static List<CardDTO> select(final List<CardDTO> all, final BiP shouldClear, final BiP shouldIgnore, final int size) {
        final List<CardDTO> rv = new ArrayList<>(size);
        for (final CardDTO card : all) {
            if (rv.size() == size) {
                break;
            }
            if (rv.size() == 0) {
                rv.add(card);
                continue;
            }

            final CardDTO last = rv.get(rv.size() - 1);
            if (shouldIgnore.test(last, card)) {
                continue;
            }
            if (shouldClear.test(last, card)) {
                rv.clear();
            }
            rv.add(card);
        }
        return rv.size() == size? rv: null;
    }

    private static List<CardDTO> removeGroup(final List<CardDTO> all, final int size) {
        final List<CardDTO> rv = select(all, (last, card) -> last.getValue() != card.getValue(), FALSE, size);
        if (rv != null) {
            all.removeAll(rv);
            return rv;
        } else {
            return null;
        }
    }

    private static List<CardDTO> straight(final List<CardDTO> in) {
        final BiP shouldIgnore = (last, card) -> last.getValue() == card.getValue();
        final BiP shouldClear = (last, card) -> !last.getValue().isNext(card.getValue());

        final List<CardDTO> all = valueSort(in);
        final List<CardDTO> bigStraight = select(all, shouldClear, shouldIgnore, 5);
        if (bigStraight != null) {
            return bigStraight;
        }
        Collections.sort(all, CardDTO::valueFirstOneDesc);
        return select(all, shouldClear, shouldIgnore, 5);
    }

    private static List<CardDTO> flush(final List<CardDTO> in) {
        return select(suitSort(in), (last, card) -> last.getSuit() != card.getSuit(), FALSE, 5);
    }

    private static List<CardDTO> topN(final List<CardDTO> in, final int n) {
        return valueSort(in).subList(0, n);
    }

    private static List<CardDTO> straightFlush(final List<CardDTO> in) {
        final List<CardDTO> rv = flush(in);
        if (rv != null) {
            // adding all cards in the flush suit in case of 5 4 3 2 ACE
            final CardDTO.Suit suit = rv.get(0).getSuit();
            return straight(in.stream().filter(c -> c.getSuit() == suit).collect(Collectors.toList()));
        }
        return null;
    }

    private static List<CardDTO> join(final List<CardDTO> com, final List<CardDTO> own) {
        final List<CardDTO> all = new ArrayList<>(com.size() + own.size());
        all.addAll(com);
        all.addAll(own);
        return Collections.unmodifiableList(all);
    }

    private static List<CardDTO> royalFlush(final List<CardDTO> in) {
        final List<CardDTO> rv = straightFlush(in);
        if (rv == null) {
            return null;
        }
        if (rv.get(0).getValue() == CardDTO.Value.ACE) {
            return rv;
        }
        return null;
    }

    private static List<CardDTO> suitSort(final List<CardDTO> in) {
        final List<CardDTO> all = new ArrayList<>(in);
        Collections.sort(all, CardDTO::suitFirstDesc);
        return all;
    }

    private static List<CardDTO> valueSort(final List<CardDTO> in) {
        final List<CardDTO> all = new ArrayList<>(in);
        Collections.sort(all, CardDTO::valueFirstDesc);
        return all;
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

    private interface BiP extends BiPredicate<CardDTO, CardDTO> {
    }
}
