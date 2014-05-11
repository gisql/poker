/*
 * @(#) RandomPlayer.java
 *
 * Copyright 2014 the poker project.
 */

package poker.player.michal;

import poker.MoveDTO;
import poker.Player;
import poker.PlayerStateDTO;
import poker.TableDTO;
import poker.TableEvent;
import poker.TableListener;
import poker.TerminationEvent;
import poker.TerminationListener;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Random but legal player
 *
 * @version created  on 10/05/14, 15:27
 */
public class RandomPlayer implements Player, TableListener, TerminationListener {
    public static final boolean DEBUG = Boolean.getBoolean("debug");

    private final String name;
    private final int foldProbability;
    private final int callProbability;

    @SuppressWarnings("UnusedDeclaration")
    public RandomPlayer() {
        this("rp/10/30");
    }

    public RandomPlayer(final String name) {
        this(name, nthNumber(name, 1, 10), nthNumber(name, 2, 30));
    }

    private static int nthNumber(final String name, final int n, final int def) {
        if (name.matches(".*/([0-9]*)/([0-9]*)")) {
            return Integer.parseInt(name.replaceAll(".*/([0-9]*)/([0-9]*)", "$" + n));
        } else {
            return def;
        }
    }

    private RandomPlayer(final String name, final int foldProbability, final int callProbability) {
        this.name = name;
        this.foldProbability = foldProbability;
        this.callProbability = callProbability;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public MoveDTO makeMove(final PlayerStateDTO pi, final TableDTO table) {
        final MoveDTO rv = doMakeMove(pi, table);
        if (DEBUG) {
            System.out.println("I, " + name() + ", makeMove(" + pi + ", " + table + ") --> " + rv);
        }
        return rv;
    }

    private MoveDTO doMakeMove(final PlayerStateDTO pi, final TableDTO table) {
        final int left = pi.getChipsLeft();
        final int mb = currentConfig().getMaximumBet();
        final int ctc = table.getChipsToCall();
        final int max = (mb > 0? Math.min(left, mb): left) - ctc;

        final int selector = ThreadLocalRandom.current().nextInt(100);
        if (selector < foldProbability || max < 0) {
            return MoveDTO.FOLD;
        } else if (selector < foldProbability + callProbability || ctc == left || max == 0) {
            return MoveDTO.CALL;
        } else {
            return MoveDTO.raise(ThreadLocalRandom.current().nextInt(max) + 1);
        }
    }

    @Override
    public void tableChanged(final TableEvent event) {
    }

    @Override
    public void terminatedBy(final TerminationEvent event) {
        if (DEBUG) {
            System.out.println(name() + " " + event);
        }
    }
}
