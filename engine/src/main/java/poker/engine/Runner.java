/*
 * @(#) Runner.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import poker.ConfigDTO;
import poker.Player;
import poker.TableEvent;
import poker.TableListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Runner {
    private static final boolean DEBUG = Boolean.getBoolean("debug");
    private static final ConfigDTO CONFIG = new ConfigDTO(1000, 100, true);

    /**
     * Give the list of player classes as arguments.  Each class can be created with the default constructor or
     * constructor taking one string argument. The argument is specified after colon: {@code
     * package.ClassName:argument}
     * <p/>
     * Note that the execution is run in parallel.  Players have to be thread-safe.  If some kind of state is required
     * for the player, please make sure it's stored in safe manner.
     * <p/>
     * The number of tables is specified through a system property {@code number-of-tables}.  Example invocation:
     * <pre>
     * $ java -classpath poker-api.jar:poker-engine.jar:poker-player.jar \
     *      -D number-of-tables=100 poker.engine.Runner \
     *      poker.player.michal.RandomPlayer:David poker.player.michal.RandomPlayer:Michal/0/80
     *
     * Starting Texas Hold'em Tournament
     * Configuration: ConfigDTO{maximumBet=100, initialChips=1000, reshuffleAfterGame=true}
     * The games area going to be played on 1000 tables.
     * Players entering the competition: [poker.player.michal.RandomPlayer:A, poker.player.michal.RandomPlayer:B/0/80]
     *
     * Games finished.  Overall statistics:
     *         D       256
     *         M/0/80  744
     * </pre>
     *
     * The statistics show player names with the total number of tables closed with the player as a winner.
     *
     * @see ThreadLocal
     * @see poker.player.michal.RandomPlayer
     */
    public static void main(String[] args) {
        final int numberOfTables = Integer.getInteger("number-of-tables", 100);
        System.out.println("Starting Texas Hold'em Tournament");
        System.out.println("Configuration: " + CONFIG);
        System.out.println("The games area going to be played on " + numberOfTables + " tables.");
        System.out.println("Players entering the competition: " + Arrays.toString(args));

        final List<Player> players = Stream.of(args).map(Runner::initPlayer).filter(p -> p != null).collect(toList());
        if (players.size() < 2) {
            System.out.println("\nTournament cancelled: not enough players left standing.");
            return;
        }

        final TableWinnerCounter twc = new TableWinnerCounter();
        Stream.generate(() -> createEngine(players, twc)).limit(numberOfTables).parallel().forEach(Engine::run);

        System.out.println("\nGames finished.  Overall statistics: ");
        twc.statistics().entrySet().stream().sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .forEach(entry -> System.out.println("\t" + entry.getKey() + "\t" + entry.getValue()));
    }

    private static Player initPlayer(final String name) {
        final String[] arr = name.split(":", 2);
        try {
            final Class<?> clazz = Class.forName(arr[0]);
            if (arr.length > 1) {
                return (Player)clazz.getConstructor(String.class).newInstance(arr[1]);
            } else {
                return (Player)clazz.newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println("Player " + name + " disqualified: " + e);
            return null;
        }
    }

    private static Engine createEngine(final List<Player> players, final TableWinnerCounter twc) {
        final List<Player> local = new ArrayList<>(players);
        Collections.shuffle(local);
        final Engine engine = new Engine(local, CONFIG);
        if (DEBUG) {
            engine.registerObserver((AllConsumingListener)System.out::println);
        }
        engine.registerObserver(twc);
        return engine;
    }
}

class TableWinnerCounter implements TableListener {
    private final ConcurrentMap<String, AtomicInteger> winners = new ConcurrentHashMap<>();

    @Override
    public void tableChanged(final TableEvent event) {
        if (event.getType() == TableEvent.Type.TABLE_CLOSED) {
            winners.computeIfAbsent(event.getPlayers().get(0), s -> new AtomicInteger()).incrementAndGet();
        }
    }

    public Map<String, Integer> statistics() {
        final Map<String, Integer> rv = new HashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : winners.entrySet()) {
            rv.put(entry.getKey(), entry.getValue().get());
        }
        return rv;
    }
}