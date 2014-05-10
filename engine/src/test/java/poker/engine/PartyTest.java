/*
 * @(#) PartyTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import org.testng.annotations.Test;
import poker.ConfigDTO;
import poker.Player;
import poker.player.michal.RandomPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PartyTest {
    @Test
    public void testParty() throws Exception {
        final Player david = new RandomPlayer("David");
        final Player michal = new RandomPlayer("Michal/10/60");
        final Player max = new RandomPlayer("Max");
        final Player noMerci = new RandomPlayer("No merci");

        for (int i = 0; i < 100; i++) {
            final List<Player> players = Arrays.asList(david, michal, max, noMerci);
            Collections.shuffle(players);
            final Engine engine = new Engine(players, new ConfigDTO(100, 50, true));
            engine.registerObserver((AllConsumingListener)System.out::println);
            engine.run();
            System.out.println("-------------------------------------\n\n");
        }
    }
}
