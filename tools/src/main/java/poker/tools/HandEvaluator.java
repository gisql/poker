/*
 * @(#) HandEvaluator.java
 *
 * Copyright 2014 the poker project.
 */

package poker.tools;

import poker.CardDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Tool class helping in evaluation of the strength of cards.
 *
 * @version created  on 11/05/14, 13:59
 */
public class HandEvaluator {
    private static final BiP FALSE = (a, b) -> false;

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

    public static List<CardDTO> onePair(final List<CardDTO> in) {
        return groups(in, 2);
    }

    public static List<CardDTO> twoPairs(final List<CardDTO> in) {
        return groups(in, 2, 2);
    }

    public static List<CardDTO> threeOfAKind(final List<CardDTO> in) {
        return groups(in, 3);
    }

    public static List<CardDTO> fullHouse(final List<CardDTO> in) {
        return groups(in, 3, 2);
    }

    public static List<CardDTO> fourOfAKind(final List<CardDTO> in) {
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

    public static List<CardDTO> straight(final List<CardDTO> in) {
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

    public static List<CardDTO> flush(final List<CardDTO> in) {
        return select(suitSort(in), (last, card) -> last.getSuit() != card.getSuit(), FALSE, 5);
    }

    public static List<CardDTO> topN(final List<CardDTO> in, final int n) {
        return valueSort(in).subList(0, n);
    }

    public static List<CardDTO> straightFlush(final List<CardDTO> in) {
        final List<CardDTO> rv = flush(in);
        if (rv != null) {
            // adding all cards in the flush suit in case of 5 4 3 2 ACE
            final CardDTO.Suit suit = rv.get(0).getSuit();
            return straight(in.stream().filter(c -> c.getSuit() == suit).collect(Collectors.toList()));
        }
        return null;
    }

    public static List<CardDTO> royalFlush(final List<CardDTO> in) {
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

    private interface BiP extends BiPredicate<CardDTO, CardDTO> {
    }
}
