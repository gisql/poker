/*
 * @(#) PotTest.java
 *
 * Copyright 2014 the poker project.
 */

package poker.engine;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PotTest {
    private final Pot pot = new Pot();

    @Test
    public void testEmpty() throws Exception {
        Assert.assertEquals(pot.total(), 0, "New pot should be empty");
        Assert.assertEquals(pot.maxContribution(), 0, "Max contribution for new pot should be 0");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("Two"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("Three"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("Four"), 0, "Contribution of non existing player should be 0");
    }

    @Test(dependsOnMethods = "testEmpty")
    public void testAddedOne() throws Exception {
        pot.add("One", 10);
        Assert.assertEquals(pot.total(), 10, "One player contributed 10");
        Assert.assertEquals(pot.maxContribution(), 10, "One player contributed 10");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 10, "Player One contributed 10");
    }

    @Test(dependsOnMethods = "testAddedOne")
    public void testAddedTwo() throws Exception {
        pot.add("Two", 15);
        Assert.assertEquals(pot.total(), 25, "Two players contributed 25");
        Assert.assertEquals(pot.maxContribution(), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 10, "Player One contributed 10");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
    }

    @Test(dependsOnMethods = "testAddedTwo")
    public void testAddedThree() throws Exception {
        pot.add("Three", 5);
        Assert.assertEquals(pot.total(), 30, "Three players contributed 30");
        Assert.assertEquals(pot.maxContribution(), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 10, "Player One contributed 10");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("Three"), 5, "Player Three contributed 5");
    }

    @Test(dependsOnMethods = "testAddedThree")
    public void testAddedFour() throws Exception {
        pot.add("Four", 20);
        Assert.assertEquals(pot.total(), 50, "Four players contributed 50");
        Assert.assertEquals(pot.maxContribution(), 20, "Player Four contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 10, "Player One contributed 10");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("Three"), 5, "Player Three contributed 5");
        Assert.assertEquals(pot.playersContribution("Four"), 20, "Player Four contributed 20");
    }

    @Test(dependsOnMethods = "testAddedFour")
    public void testRemoveOne() throws Exception {
        pot.removePlayer("One");
        Assert.assertEquals(pot.total(), 50, "Four players contributed 50");
        Assert.assertEquals(pot.maxContribution(), 20, "Player Four contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 0, "Player One is removed, so it doesn't exist in game");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("Three"), 5, "Player Three contributed 5");
        Assert.assertEquals(pot.playersContribution("Four"), 20, "Player Four contributed 20");
    }

    @Test(dependsOnMethods = "testRemoveOne")
    public void testRemoveFour() throws Exception {
        pot.removePlayer("Four");
        Assert.assertEquals(pot.total(), 50, "Four players contributed 50");
        Assert.assertEquals(pot.maxContribution(), 15, "Player Four, removed, the next best is Two who contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 0, "Player One is removed, so it doesn't exist in game");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("Three"), 5, "Player Three contributed 5");
        Assert.assertEquals(pot.playersContribution("Four"), 0, "Player Four is removed, so it doesn't exist in game");
    }

    @Test(dependsOnMethods = "testRemoveFour")
    public void testDivide() throws Exception {
        final Map<String, Integer> spoils = pot.divide(Stream.of("Two", "Three").collect(Collectors.toSet()));
        Assert.assertEquals(spoils.get("Two").intValue(), 25);
        Assert.assertEquals(spoils.get("Three").intValue(), 25);
        Assert.assertNull(spoils.get("Four"));

        Assert.assertEquals(pot.total(), 50, "Four players contributed 50");
        Assert.assertEquals(pot.maxContribution(), 15, "Player Four, removed, the next best is Two who contributed 15");
        Assert.assertEquals(pot.playersContribution("non-existing"), 0, "Contribution of non existing player should be 0");
        Assert.assertEquals(pot.playersContribution("One"), 0, "Player One is removed, so it doesn't exist in game");
        Assert.assertEquals(pot.playersContribution("Two"), 15, "Player Two contributed 15");
        Assert.assertEquals(pot.playersContribution("Three"), 5, "Player Three contributed 5");
        Assert.assertEquals(pot.playersContribution("Four"), 0, "Player Four is removed, so it doesn't exist in game");
    }

    @Test(dependsOnMethods = "testDivide")
    public void testClear() throws Exception {
        pot.clear();
        testEmpty();
    }
}
