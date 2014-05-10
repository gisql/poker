/*
 * @(#) AllConsumingListener.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.BaseEvent;
import poker.BidEvent;
import poker.BidListener;
import poker.CardEvent;
import poker.CardListener;
import poker.TableEvent;
import poker.TableListener;
import poker.TerminationEvent;
import poker.TerminationListener;

/**
 * Trait for channeling all known game events into a single method.
 *
 * @version created on 05/05/14, 15:17
 */
public interface AllConsumingListener extends BidListener, CardListener, TableListener, TerminationListener {
    void consume(final BaseEvent event);

    @Override
    default void bidMade(final BidEvent event) {
        consume(event);
    }

    @Override
    default void cardsChanged(final CardEvent event) {
        consume(event);
    }

    @Override
    default void tableChanged(final TableEvent event) {
        consume(event);
    }

    @Override
    default void terminatedBy(final TerminationEvent event) {
        consume(event);
    }
}
