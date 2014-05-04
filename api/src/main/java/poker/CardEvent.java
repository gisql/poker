/*
 * @(#) CardEvent.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DTO describing events affecting the game table.
 *
 * @version created on 2014-05-04, 23:00
 */
public class CardEvent {
    private final Type type;
    private final String player;
    private final List<CardDTO> cards;

    public CardEvent(final Type type, final String player, final List<CardDTO> cards) {
        this.type = type;
        this.player = player;
        this.cards = Collections.unmodifiableList(cards);
    }

    @Override
    public String toString() {
        return player + " " + type.name() + " " + Arrays.toString(cards.toArray(new CardDTO[cards.size()]));
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public String getPlayer() {
        return player;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        COMMUNITY_CARD, PRIVATE_CARD, HAND_SHOWN
    }
}
