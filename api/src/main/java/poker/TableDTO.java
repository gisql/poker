/*
 * Copyright
 */
package poker;

import java.util.Collections;
import java.util.List;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 11:26
 */
public class TableDTO {
    private final List<CardDTO> communityCards;
    private final int pot;
    private final int lastBet;

    public TableDTO(final List<CardDTO> communityCards, final int pot, final int lastBet) {
        this.communityCards = Collections.unmodifiableList(communityCards);
        this.pot = pot;
        this.lastBet = lastBet;
    }

    public List<CardDTO> getCommunityCards() {
        return communityCards;
    }

    public int getLastBet() {
        return lastBet;
    }

    public int getPot() {
        return pot;
    }
}
