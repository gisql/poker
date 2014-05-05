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
    private final int pot;
    private final int chipsToCall;
    private final List<CardDTO> communityCards;

    public TableDTO(final List<CardDTO> communityCards, final int pot, final int chipsToCall) {
        this.communityCards = Collections.unmodifiableList(communityCards);
        this.pot = pot;
        this.chipsToCall = chipsToCall;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableDTO{");
        sb.append("pot=").append(pot);
        sb.append(", chipsToCall=").append(chipsToCall);
        sb.append(", communityCards=").append(CardDTO.toString(communityCards));
        sb.append('}');
        return sb.toString();
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
