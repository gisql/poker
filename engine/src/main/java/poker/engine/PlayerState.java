/*
 * @(#) PlayerState.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.CardDTO;
import poker.CardEvent;
import poker.CardListener;
import poker.Player;
import poker.PlayerStateDTO;
import poker.TerminationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mutable player state used internally by the engine
 *
 * @version created on 04/05/14, 10:40
 */
class PlayerState {
    private final Player player;

    private int chips;
    private List<CardDTO> cards;

    public PlayerState(final Player player, final int chips) {
        this.player = player;
        this.chips = chips;
        cards = new ArrayList<>();
    }

    public PlayerStateDTO toDTO() {
        return new PlayerStateDTO(new ArrayList<>(cards), chips);
    }

    @Override
    public String toString() {
        final CardDTO[] cds = cards.toArray(new CardDTO[cards.size()]);
        Arrays.sort(cds);
        return player.name() + "(" + chips + ") " + Arrays.toString(cds);
    }

    /**
     * Attempts to pay {@code amount} to the pot.  If player doesn't have enough money, he is notified that he lost and
     * method returns false;
     *
     * @param pot    pot to which the payment is to be made
     * @param amount amount to be paid
     *
     * @return true if payment is successful
     */
    public boolean attemptPayment(final Pot pot, final int amount) {
        if (chips < amount) {
            return false;
        }
        chips -= amount;
        pot.add(player.name(), amount);
        return true;
    }

    public void reward(final Integer chips) {
        this.chips += chips;
    }

    public void lost() {
        if (player instanceof TerminationListener) {
            ((TerminationListener)player).lost();
        }
    }

    public void killed(final String reason) {
        if (player instanceof TerminationListener) {
            ((TerminationListener)player).die(reason);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setCards(final List<CardDTO> cards) {
        this.cards = cards;
        if (player instanceof CardListener) {
            ((CardListener)player).event(new CardEvent(CardEvent.Type.PRIVATE_CARD, player.name(), cards));
        }
    }

    List<CardDTO> getCards() {
        return cards;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(final int chips) {
        this.chips = chips;
    }
}
