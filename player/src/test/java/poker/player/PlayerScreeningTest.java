/*
 * @(#) PlayerScreeningTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.player;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poker.CardDTO;
import poker.ConfigDTO;
import poker.MoveDTO;
import poker.Player;
import poker.PlayerStateDTO;
import poker.TableDTO;
import poker.TableListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This test verifies the basic functionality of player class.  In order for the player to be accepted for the
 * tournament, it has to have a default constructor.  If a given player class is to be entered several times, the class
 * has to implement constructor with one string parameter ALONGSIDE THE DEFAULT CONSTRUCTOR.
 * <p/>
 * Needless to say, players should always make legal move.  If a player makes an illegal move, it's removed from the
 * tournament via player death.
 * <p/>
 * This class is generic (out of the necessity).  Please create your own specific tests and expand this test only if
 * it's applicable for ALL player classes.
 */
public class PlayerScreeningTest {
    public static final int MAX_TRIES = 100000;
    public static final int MAXIMUM_BET = 50;
    public static final int INITIAL_CHIPS = 100;
    private final ConfigDTO cfg = new ConfigDTO(INITIAL_CHIPS, MAXIMUM_BET, true);
    private List<CardDTO> deck;

    @BeforeClass
    public void setUp() throws Exception {
        final TableListener tl = e -> {
        };
        tl.configChanged(cfg);
        deck = CardDTO.deck();
    }

    @DataProvider
    public Object[][] classNamesDP() {
        return new Object[][] {
                {"poker.player.michal.RandomPlayer"},
                {"poker.player.cyril.BasicPlayer"}
        };
    }

    @Test(dataProvider = "classNamesDP")
    public void testClassExists(final String name) throws Exception {
        Class.forName(name);
    }

    @Test(dataProvider = "classNamesDP")
    public void testIsPlayerClass(final String name) throws Exception {
        Player.class.isAssignableFrom(Class.forName(name));
    }

    @Test(dataProvider = "classNamesDP")
    public void testHasDefaultConstructor(final String name) throws Exception {
        final Class<?> clazz = Class.forName(name);
        final Object p = clazz.newInstance();
        Assert.assertTrue(p instanceof Player);
    }

    @Test(dataProvider = "classNamesDP")
    public void testConstructorWithStringWorks(final String name) throws Exception {
        final Constructor<?> strConstructor = Class.forName(name).getConstructor(String.class);
        if (strConstructor == null) {
            return;
        }
        final Object p = strConstructor.newInstance("test");
        Assert.assertTrue(p instanceof Player);
    }

    @Test(dataProvider = "classNamesDP")
    public void testConstructorWithStringNames(final String name) throws Exception {
        final Constructor<?> strConstructor = Class.forName(name).getConstructor(String.class);
        if (strConstructor == null) {
            return;
        }
        final Player player = (Player)strConstructor.newInstance("test");
        Assert.assertNotNull(player.name(), "Player name cannot be null");
        Assert.assertFalse(player.name().trim().isEmpty(), "Player name cannot be empty");
    }

    @Test(dataProvider = "classNamesDP")
    public void testLegalName(final String name) throws Exception {
        final Player player = (Player)Class.forName(name).newInstance();
        Assert.assertNotNull(player.name(), "Player name cannot be null");
        Assert.assertFalse(player.name().trim().isEmpty(), "Player name cannot be empty");
    }

    @Test(dataProvider = "classNamesDP")
    public void testTableListener(final String name) throws Exception {
        final Object o = Class.forName(name).newInstance();
        if (!(o instanceof TableListener)) {
            return;
        }
        final TableListener tl = (TableListener)o;
        tl.configChanged(cfg);
        Assert.assertEquals(tl.currentConfig(), cfg, "Config returned by currentConfig should match the one passed through listener");
    }

    @Test(dataProvider = "classNamesDP")
    public void testLegality(final String name) throws Exception {
        final Player player = (Player)Class.forName(name).newInstance();

        final ConfigDTO config = new ConfigDTO(INITIAL_CHIPS, MAXIMUM_BET, true);
        if (player instanceof TableListener) {
            final TableListener tl = (TableListener)player;
            tl.configChanged(config);
        }

        final Random rnd = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            final int chipsToCall = rnd.nextInt(MAXIMUM_BET);
            final int chipsLeft = rnd.nextInt(INITIAL_CHIPS);
            final boolean hc = rnd.nextBoolean();
            final PlayerStateDTO pi = new PlayerStateDTO(cards(hc? 0: 2), chipsLeft);
            final TableDTO table = new TableDTO(cards(hc? 0: rnd.nextInt(6)), chipsToCall, chipsToCall);
            final MoveDTO move = player.makeMove(pi, table);

            switch (move.getType()) {
            case ALL_IN:
                Assert.assertTrue(table.getChipsToCall() > chipsLeft, "All in is only legal if player doesn't have enough chips to make another move");
                Assert.fail("All in is not implemented in the current version of engine.");
            case RAISE:
                Assert.assertTrue(move.getChips() > 0, "Choose CALL if you want to raise with no chips");
            case CALL:
                Assert.assertTrue(move.getChips() + chipsToCall <= chipsLeft, "Player doesn't have enough money to make move: " + move);
                Assert.assertTrue(move.getChips() <= MAXIMUM_BET, "Player cannot bet more than " + MAXIMUM_BET);
            case FOLD:
                break; // always legal
            }
        }
    }


    private List<CardDTO> cards(final int n) {
        Collections.shuffle(deck);
        final List<CardDTO> rv = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            rv.add(deck.get(i));
        }
        return rv;
    }
}
