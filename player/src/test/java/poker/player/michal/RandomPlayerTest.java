/*
 * @(#) RandomPlayerTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.player.michal;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import poker.ConfigDTO;
import poker.MoveDTO;
import poker.PlayerStateDTO;
import poker.TableDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomPlayerTest {
    public static final ConfigDTO CONFIG = new ConfigDTO(1000, -1, true);
    public static final int MAX_MOVES = 100000;
    private final Map<MoveDTO.MoveType, AtomicInteger> counters = new HashMap<>();

    @BeforeMethod
    public void setUp() throws Exception {
        counters.clear();
        counters.put(MoveDTO.MoveType.ALL_IN, new AtomicInteger());
        counters.put(MoveDTO.MoveType.CALL, new AtomicInteger());
        counters.put(MoveDTO.MoveType.RAISE, new AtomicInteger());
        counters.put(MoveDTO.MoveType.FOLD, new AtomicInteger());
    }

    @DataProvider
    public Object[][] probabilitiesDP() {
        return new Object[][] {
                {20, 30, 50},
                {0, 50, 50},
                {10, 0, 90},
                {90, 5, 5},
        };
    }

    @Test(dataProvider = "probabilitiesDP")
    public void testCounter(final int foldP, final int callP, final int raiseP) throws Exception {
        final RandomPlayer rp = new RandomPlayer("rp/" + foldP + "/" + callP);
        rp.configChanged(CONFIG);
        final PlayerStateDTO pi = new PlayerStateDTO(new ArrayList<>(), 10000);
        final TableDTO table = new TableDTO(new ArrayList<>(), 0, 0);
        for (int i = 0; i < MAX_MOVES; i++) {
            counters.get(rp.makeMove(pi, table).getType()).incrementAndGet();
        }
        percentageMatches(MoveDTO.MoveType.ALL_IN, 0);
        percentageMatches(MoveDTO.MoveType.FOLD, foldP);
        percentageMatches(MoveDTO.MoveType.CALL, callP);
        percentageMatches(MoveDTO.MoveType.RAISE, raiseP);
    }

    private void percentageMatches(final MoveDTO.MoveType type, final int expected) {
        final int actual = (int)Math.round(100.0 * counters.get(type).get() / MAX_MOVES);
        Assert.assertEquals(actual, expected, "Expected " + expected + "% for move type: " + type);
    }
}
