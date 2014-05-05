/*
 * @(#) TerminationEvent.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

/**
 * A single termination event data.
 *
 * @version created on 05/05/14, 15:21
 */
public class TerminationEvent extends BaseEvent {
    private final Type type;
    private final String reason;

    public TerminationEvent(final Type type, final String reason) {
        this.type = type;
        this.reason = reason;
    }

    public static TerminationEvent death(final String reason) {
        return new TerminationEvent(Type.DEATH, reason);
    }

    public static TerminationEvent defeat(final String reason) {
        return new TerminationEvent(Type.DEFEAT, reason);
    }

    @Override
    public String toString() {
        return type.name() + ": " + reason;
    }

    public Type getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public static enum Type {
        DEATH,
        DEFEAT,
    }
}
