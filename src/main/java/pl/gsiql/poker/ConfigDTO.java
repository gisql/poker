/*
 * Copyright
 */
package pl.gsiql.poker;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 11:31
 */
public class ConfigDTO {
    private final int version;
    private final int numberOfDecks;
    private final int maximumBet;
    private final int initialChips;
    private final boolean reshuffleAfterGame;

    public ConfigDTO(final int initialChips, final int version, final int numberOfDecks, final int maximumBet, final boolean reshuffleAfterGame) {
        this.initialChips = initialChips;
        this.version = version;
        this.numberOfDecks = numberOfDecks;
        this.maximumBet = maximumBet;
        this.reshuffleAfterGame = reshuffleAfterGame;
    }

    public int getInitialChips() {
        return initialChips;
    }

    public int getMaximumBet() {
        return maximumBet;
    }

    public int getNumberOfDecks() {
        return numberOfDecks;
    }

    public boolean isReshuffleAfterGame() {
        return reshuffleAfterGame;
    }

    public int getVersion() {
        return version;
    }
}
