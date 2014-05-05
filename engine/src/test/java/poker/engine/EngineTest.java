/*
 * @(#) EngineTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import org.testng.Assert;
import org.testng.annotations.Test;
import poker.CardDTO;
import poker.ConfigDTO;
import poker.MoveDTO;
import poker.Player;
import poker.PlayerStateDTO;
import poker.TableDTO;
import poker.TableEvent;
import poker.TableListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static poker.CardDTO.Suit.CLUBS;
import static poker.CardDTO.Suit.DIAMONDS;
import static poker.CardDTO.Suit.HEARTS;
import static poker.CardDTO.Suit.SPADES;
import static poker.CardDTO.Value.ACE;
import static poker.CardDTO.Value.FOUR;
import static poker.CardDTO.Value.JACK;
import static poker.CardDTO.Value.KING;
import static poker.CardDTO.Value.NINE;
import static poker.CardDTO.Value.QUEEN;
import static poker.CardDTO.Value.SEVEN;
import static poker.CardDTO.Value.SIX;
import static poker.CardDTO.Value.TEN;
import static poker.CardDTO.Value.THREE;
import static poker.CardDTO.Value.TWO;

public class EngineTest {
    @Test(timeOut = 1000)
    public void testAlwaysRaising() throws Exception {
        final TableObserver observer = new TableObserver();
        final List<Player> players = Arrays.asList(arp(1), arp(2), arp(3), observer);
        final Engine engine = new Engine(players, new ConfigDTO(15, -1, true));
        engine.run();

        final List<TableEvent> actions = observer.getActions();
        final TableEvent last = actions.get(actions.size() - 1);

        Assert.assertEquals(last.getType(), TableEvent.Type.TABLE_CLOSED, "The last action should be informing about the global table winner");

        // Small and big blinds make raising by 10 illegal removing (killing) the 2 ARPs,
        // which means that dealer should win the first game with arp1 and observer left in table.
        // Since observer always folds, the arp1 should win in the end by taking all chips through the forced bets.
        Assert.assertEquals(last.getPlayers().get(0), "AlwaysRaisingPlayer1", "The player 1 should have won");
    }

    private AlwaysRaisingPlayer arp(int i) {
        return new AlwaysRaisingPlayer(10, "AlwaysRaisingPlayer" + i);
    }

    @Test
    public void testAlwaysCalling() throws Exception {
        final TableObserver observer = new TableObserver();
        final List<Player> players = Arrays.asList(acp(1), acp(2), observer);
        final Engine engine = new Engine(players, new ConfigDTO(15, -1, true));
        engine.setDealer(new Dealer() {
            private AtomicInteger ai = new AtomicInteger(-1);
            private List<CardDTO> deck = Arrays.asList(
                    card(HEARTS, ACE), card(HEARTS, KING), // acp1
                    card(SPADES, TWO), card(CLUBS, THREE), // acp2
                    card(SPADES, SEVEN), card(CLUBS, NINE), // observer
                    card(HEARTS, QUEEN), card(HEARTS, JACK), card(HEARTS, TEN), card(DIAMONDS, FOUR), card(DIAMONDS, SIX) // community
            );

            public void burn() {
                // nothing
            }

            @Override
            public void shuffle() {
                deck = CardDTO.deck();
                Collections.shuffle(deck);
                ai.set(0);
            }

            @Override
            public CardDTO deal() {
                return deck.get(ai.incrementAndGet());
            }
        });
        engine.run();

        final TableEvent won = observer.getActions().stream().filter(a -> a.getType() == TableEvent.Type.GAME_WON)
                .limit(1).collect(Collectors.<TableEvent>toList()).get(0);
        Assert.assertEquals(won.getPlayers().get(0), "AlwaysCallingPlayer1", "First game should win player 1");
    }

    private CardDTO card(final CardDTO.Suit suit, final CardDTO.Value value) {
        return new CardDTO(suit, value);
    }

    private AlwaysCallingPlayer acp(int i) {
        return new AlwaysCallingPlayer("AlwaysCallingPlayer" + i);
    }

}

interface FoldingPlayer extends Player {
    default MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table) {
        return MoveDTO.FOLD;
    }
}

class TableObserver implements FoldingPlayer, TableListener {
    private final List<TableEvent> actions = new LinkedList<>();

    @Override
    public void tableChanged(final TableEvent event) {
        actions.add(event);
    }

    List<TableEvent> getActions() {
        return actions;
    }
}

abstract class NamedPlayer implements Player {
    private final String name;

    protected NamedPlayer(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}

class AlwaysRaisingPlayer extends NamedPlayer {
    private final int by;

    AlwaysRaisingPlayer(final int by, final String name) {
        super(name);
        this.by = by;
    }

    @Override
    public MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table) {
        return MoveDTO.raise(by);
    }
}

class AlwaysCallingPlayer extends NamedPlayer {
    protected AlwaysCallingPlayer(final String name) {
        super(name);
    }

    @Override
    public MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table) {
        return MoveDTO.CALL;
    }
}
