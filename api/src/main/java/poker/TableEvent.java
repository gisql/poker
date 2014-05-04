/*
 * @(#) TableEvent.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import java.util.Collections;
import java.util.List;

/**
 * DTO describing events affecting the game table.
 *
 * @version created on 2014-04-17, 11:35
 */
public class TableEvent {
    private final Type type;
    private final List<String> players;

    public TableEvent(final Type type, final List<String> players) {
        this.type = type;
        this.players = Collections.unmodifiableList(players);
    }

    @Override
    public String toString() {
        return type.name() + " [" + String.join(", ", players) + "]";
    }

    public List<String> getPlayers() {
        return players;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        TABLE_CREATED,
        TABLE_CLOSED,
        GAME_STARTED,
        GAME_WON,
        PLAYER_REMOVED,
        PLAYER_LOST,
    }
}
