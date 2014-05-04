/*
 * @(#) TableDTO.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

import java.util.Collections;
import java.util.List;

/**
 * DTO containing all table information visible to a player.
 *
 * @version created on 2014-04-17, 11:26
 */
public class TableDTO {
    private final List<CardDTO> communityCards;
    private final int pot;
    private final int chipsToCall;

    public TableDTO(final List<CardDTO> communityCards, final int pot, final int chipsToCall) {
        this.communityCards = Collections.unmodifiableList(communityCards);
        this.pot = pot;
        this.chipsToCall = chipsToCall;
    }

    public List<CardDTO> getCommunityCards() {
        return communityCards;
    }

    public int getChipsToCall() {
        return chipsToCall;
    }

    public int getPot() {
        return pot;
    }
}
