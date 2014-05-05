/*
 * @(#) TerminationListener.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

public interface TerminationListener {
    void terminatedBy(final TerminationEvent event);
}
