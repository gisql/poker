/*
 * @(#) Pot.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * A mutable representation of the table's pot.
 *
 * @version created on 26/04/14, 12:38
 */
class Pot {
    public static final AtomicInteger ZERO = new AtomicInteger(0);

    private final AtomicInteger total = new AtomicInteger();
    private final Map<String, AtomicInteger> contributions = new HashMap<>();

    public int playersContribution(final String name) {
        return contributions.getOrDefault(name, ZERO).get();
    }

    public int maxContribution() {
        return Stream.of(contributions.values().toArray(new AtomicInteger[contributions.values().size()]))
                .mapToInt(AtomicInteger::get).max().getAsInt();
    }

    public void clear() {
        contributions.clear();
        total.set(0);
    }

    public int total() {
        return total.get();
    }

    public void add(final String name, final int chips) {
        contributions.computeIfAbsent(name, s -> new AtomicInteger()).addAndGet(chips);
        total.addAndGet(chips);
    }

    public void removePlayer(final String name) {
        contributions.remove(name);
    }

    public boolean equalised() {
        int amount = -1;
        for (final AtomicInteger ai : contributions.values()) {
            if (amount < 0) {
                amount = ai.get();
            } else {
                if (amount != ai.get()) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map<String, Integer> divide(final Set<String> winners) {
        // split pots not implemented in this version

        final int share = total() / winners.size(); // it's rounded down --- 'casino' wins the rest ;)
        final Map<String, Integer> rv = new HashMap<>(winners.size());
        for (final String name : winners) {
            rv.put(name, share);
        }

        return rv;
    }
}
