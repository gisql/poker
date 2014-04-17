/*
 * Copyright
 */
package poker.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import poker.BidListener;
import poker.ConfigDTO;
import poker.MoveDTO;
import poker.Player;
import poker.TableListener;
import poker.TerminationListener;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 15:42
 */
public class Engine {
    public static final int VERSION = 1;

    private final List<Player> players;
    private final List<BidListener> bidListeners = new ArrayList<>();
    private final List<TableListener> tableListeners = new ArrayList<>();
    private final List<TerminationListener> terminationListeners = new ArrayList<>();

    private final ConfigDTO config;

    public Engine(final List<Player> players) {
        this.players = players;
        config = new ConfigDTO(1000, VERSION, 2, 200, true);
        for (final Player player : players) {
            if (player instanceof BidListener) {
                bidListeners.add((BidListener) player);
            }
            if (player instanceof TableListener) {
                tableListeners.add((TableListener) player);
            }
            if (player instanceof TerminationListener) {
                terminationListeners.add((TerminationListener) player);
            }
        }
    }

    public void run() {
        newTable();
        while (players.size() > 1) {
            newGame();
            gameWinner(playAGame());
        }
        tableWinner(players.get(0));
    }

    private void tableWinner(final Player player) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void gameWinner(final Player player) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void newGame() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void newTable() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private Player playAGame() {
        final List<Player> gamePlayers = new ArrayList<>(players);
        try {
            initialPayments(gamePlayers);
            dealPrivateCards(gamePlayers);
            roundOfBetting(gamePlayers);

            dealCommunityCard();
            dealCommunityCard();
            dealCommunityCard();
            roundOfBetting(gamePlayers);

            dealCommunityCard();
            roundOfBetting(gamePlayers);

            dealCommunityCard();
            roundOfBetting(gamePlayers);

            return showdown(gamePlayers);
        } catch (OnePlayerLeftException e) {
            // premature end of game --- the last standing wins
            return gamePlayers.get(0);
        }
    }

    private Player showdown(final List<Player> gamePlayers) {
        return null;
    }

    private void dealCommunityCard() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void dealPrivateCards(final List<Player> gamePlayers) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void initialPayments(final List<Player> gamePlayers) throws OnePlayerLeftException {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void roundOfBetting(final List<Player> gamePlayers) throws OnePlayerLeftException {
        do {
            final Iterator<Player> itr = gamePlayers.iterator();
            while (itr.hasNext()) {
                final Player player = itr.next();
                final MoveDTO move = player.makeMove(null, null);
                if (!legal(move)) {
                    itr.remove();
                    killPlayer(player);
                    continue;
                }
                if (move.getType() == MoveDTO.MoveType.FOLD) {
                    itr.remove();
                }
                moveMade(move);
            }
        } while (!playersEqualised(gamePlayers));
    }

    private void moveMade(final MoveDTO move) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void killPlayer(final Player player) {
        players.remove(player);
    }

    private boolean legal(final MoveDTO move) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private boolean playersEqualised(final List<Player> gamePlayers) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
