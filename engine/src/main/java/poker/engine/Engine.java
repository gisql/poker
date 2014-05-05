/*
 * @(#) Engine.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.BidEvent;
import poker.BidListener;
import poker.CardDTO;
import poker.CardEvent;
import poker.CardListener;
import poker.ConfigDTO;
import poker.MoveDTO;
import poker.Player;
import poker.TableDTO;
import poker.TableEvent;
import poker.TableListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * The poker playing engine.
 *
 * @version created on 2014-04-17, 15:42
 */
public class Engine {
    public static final int ANTE = 1;
    public static final int SMALL_BLIND = 2;
    public static final int BIG_BLIND = 4;

    private final List<PlayerState> players;
    private final List<BidListener> bidListeners = new ArrayList<>();
    private final List<TableListener> tableListeners = new ArrayList<>();
    private final List<CardListener> cardListeners = new ArrayList<>();

    private final ConfigDTO config;

    private Dealer dealer;

    private List<PlayerState> gamePlayers;
    private final Pot pot = new Pot();
    private final List<CardDTO> communityCards = new ArrayList<>(5);

    public Engine(final List<Player> players, final ConfigDTO config) {
        this.config = config;
        this.players = initPlayers(players);
        for (final Player player : players) {
            registerObserver(player);
        }
        dealer = new CasinoDealer(1);
        tableListeners.forEach(p -> p.configChanged(config));
    }

    public void registerObserver(final Object observer) {
        if (observer instanceof BidListener) {
            bidListeners.add((BidListener)observer);
        }
        if (observer instanceof TableListener) {
            tableListeners.add((TableListener)observer);
        }
        if (observer instanceof CardListener) {
            cardListeners.add((CardListener)observer);
        }
    }

    private List<PlayerState> initPlayers(final List<Player> players) {
        final List<PlayerState> rv = new ArrayList<>(players.size());
        final Set<String> names = new HashSet<>();
        for (final Player player : players) {
            rv.add(new PlayerState(player, config.getInitialChips()));
            names.add(player.name());
        }
        if (names.size() != players.size()) {
            throw new IllegalArgumentException("Player names are not unique");
        }
        final int maxPlayers = (52 - 4 - 2 - 2) / 2;
        if (players.size() > maxPlayers) {
            throw new IllegalArgumentException("Too many players for one table.  Max is: " + maxPlayers);
        }
        return rv;
    }

    public void run() {
        createTable();
        while (players.size() > 1) {
            final List<String> winners = playAGame();

            notifyTable(TableEvent.Type.GAME_WON, winners);
            rewardWinners(winners);

            prepareNewGame();
        }
        notifyTable(TableEvent.Type.TABLE_CLOSED, asList(players.get(0).getPlayer().name()));
    }

    private List<String> playAGame() {
        gamePlayers = new ArrayList<>(players);
        try {
            initialPayments();
            dealPrivateCards();
            roundOfBetting();

            dealCommunityCard();
            dealCommunityCard();
            dealCommunityCard();
            roundOfBetting();

            dealCommunityCard();
            roundOfBetting();

            dealCommunityCard();
            roundOfBetting();

            return showdown();
        } catch (OnePlayerLeftException e) {
            // premature end of game --- the last standing wins
            return asList(gamePlayers.get(0).getPlayer().name());
        }
    }

    private void rewardWinners(final List<String> in) {
        final Set<String> winners = new HashSet<>(in);

        final Map<String, Integer> winnings = pot.divide(winners);
        for (final PlayerState player : players) {
            final String name = player.getPlayer().name();
            if (winnings.containsKey(name)) {
                player.reward(winnings.get(name));
            }
        }
    }

    private void prepareNewGame() {
        if (players.size() < 2) {
            return;
        }
        players.add(players.remove(0));
        if (config.isReshuffleAfterGame()) {
            dealer.shuffle();
        }

        pot.clear();
        communityCards.clear();

        notifyTable(TableEvent.Type.GAME_STARTED, playerNames(players));
    }

    private void createTable() {
        pot.clear();
        notifyTable(TableEvent.Type.TABLE_CREATED, playerNames(players));
    }

    private void notifyTable(final TableEvent.Type eventType, final List<String> names) {
        final TableEvent te = new TableEvent(eventType, names);
        tableListeners.forEach(p -> p.tableChanged(te));
    }

    private List<String> playerNames(final List<PlayerState> active) {
        return active.stream().map(a -> a.getPlayer().name()).collect(Collectors.<String>toList());
    }

    private List<String> showdown() {
        final List<PokerHand> hands = new ArrayList<>(gamePlayers.size());
        for (final PlayerState player : gamePlayers) {
            final String name = player.getPlayer().name();
            final PokerHand hand = PokerHand.selectBest(name, communityCards, player.getCards());
            hands.add(hand);
            cardListeners.forEach(cl -> cl.cardsChanged(new CardEvent(CardEvent.Type.HAND_SHOWN, name, player.getCards())));
        }
        Collections.sort(hands, Comparator.reverseOrder());

        final List<String> rv = new LinkedList<>();
        PokerHand last = hands.get(0);
        rv.add(last.getOwner());
        for (int i = 1; i < hands.size(); i++) {
            final PokerHand cur = hands.get(i);
            if (last.compareTo(cur) != 0) {
                break;
            }
            rv.add(cur.getOwner());
            last = cur;
        }
        return rv;
    }

    private void dealCommunityCard() {
        final CardDTO card = dealer.deal();
        communityCards.add(card);
        cardListeners.forEach(cl -> cl.cardsChanged(new CardEvent(CardEvent.Type.COMMUNITY_CARD, "all", asList(card))));
    }

    private void dealPrivateCards() {
        for (final PlayerState player : gamePlayers) {
            player.setCards(Arrays.asList(dealer.deal(), dealer.deal()));
        }
    }

    /**
     * Initial payments consist of:<ul> <li>One chip from each of players</li> <li>Two chips from the player next to the
     * dealer (small blind)</li> <li>Four chips from the player next to the small blind</li> </ul>
     * <p/>
     * Note that if there are not enough players (less than 3), the paying goes around to the dealer.  If at any point,
     * a player is incapable of paying, he is removed from the game (looses).
     */
    private void initialPayments() throws OnePlayerLeftException {
        for (final PlayerState player : new ArrayList<>(gamePlayers)) {
            assureTwoPlayers();
            if (!player.attemptPayment(pot, ANTE)) {
                killPlayer(player, "Not enough money for ante.");
            }
        }

        final PlayerState smallBlind = gamePlayers.get(1 % gamePlayers.size());
        if (!smallBlind.attemptPayment(pot, SMALL_BLIND)) {
            playerLostGame(smallBlind, "Not enough money for small blind");
        }
        assureTwoPlayers();

        final PlayerState bigBlind = gamePlayers.get(2 % gamePlayers.size());
        if (!bigBlind.attemptPayment(pot, BIG_BLIND)) {
            playerLostGame(bigBlind, "Not enough money for big blind");
        }
        assureTwoPlayers();
    }

    private void killPlayer(final PlayerState player, final String reason) {
        players.remove(player);
        removeFromGame(player);

        notifyTable(TableEvent.Type.PLAYER_REMOVED, asList(player.getPlayer().name()));
        player.killed(reason);
    }

    private void playerLostGame(final PlayerState player, final String reason) {
        removeFromGame(player);

        notifyTable(TableEvent.Type.PLAYER_LOST, asList(player.getPlayer().name()));
        player.lost(reason);
    }

    private void removeFromGame(final PlayerState player) {
        if (gamePlayers.contains(player)) {
            gamePlayers.remove(player);
        }
        pot.removePlayer(player.getPlayer().name());
    }

    private void roundOfBetting() throws OnePlayerLeftException {
        do {
            for (final PlayerState player : new ArrayList<>(gamePlayers)) {
                assureTwoPlayers();

                final int chipsDelta = pot.maxContribution() - pot.playersContribution(player.getPlayer().name());
                final TableDTO table = new TableDTO(communityCards, pot.total(), chipsDelta);

                final MoveDTO move = player.getPlayer().makeMove(player.toDTO(), table);
                moveMade(player, move);
            }
        } while (!playersEqualised());
    }

    private void assureTwoPlayers() throws OnePlayerLeftException {
        if (gamePlayers.size() == 1) {
            throw new OnePlayerLeftException();
        }
    }

    private void moveMade(final PlayerState player, final MoveDTO move) {
        if (!legal(move)) {
            killPlayer(player, "Your move is illegal");
            return;
        }

        final String name = player.getPlayer().name();
        final int max = pot.maxContribution();
        final int pc = pot.playersContribution(name);
        if (move == MoveDTO.FOLD) {
            bidListeners.forEach(p -> p.bidMade(new BidEvent(name, move)));
            playerLostGame(player, "Player folded");
            return;
        } else if (move == MoveDTO.CALL && pc < max) {
            if (!player.attemptPayment(pot, max - pc)) {
                killPlayer(player, "Illegal move: not enough money to call");
                return;
            }
        } else if (move.getType() == MoveDTO.MoveType.RAISE) {
            if (!player.attemptPayment(pot, max - pc + move.getChips())) {
                killPlayer(player, "Illegal move: not enough money to raise by " + move.getChips());
                return;
            }
        } else if (move == MoveDTO.ALL_IN) {
            throw new IllegalStateException("All in not implemented in this version and should be filter out by legality check");
        }
        bidListeners.forEach(p -> p.bidMade(new BidEvent(name, move)));
    }

    private boolean legal(final MoveDTO move) {
        switch (move.getType()) {
        case ALL_IN:
            return false;   // all in not implemented
        case RAISE:
            return config.getMaximumBet() < 1 || move.getChips() <= config.getMaximumBet();
        case CALL:
            return true;
        case FOLD:
            return true;
        default:
            throw new IllegalStateException("Unknown move type: " + move.getType());
        }
    }

    private boolean playersEqualised() {
        return pot.equalised();
    }

    public void setDealer(final Dealer dealer) {
        this.dealer = dealer;
    }
}
