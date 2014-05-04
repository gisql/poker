/*
 * @(#) ConfigDTO.java
 *
 * Copyright 2014 the poker project.
 */

package poker;

/**
 * DTO with table configuration.
 *
 * @version created on 2014-04-17, 11:31
 */
public class ConfigDTO {
    private final int maximumBet;
    private final int initialChips;
    private final boolean reshuffleAfterGame;

    public ConfigDTO(final int initialChips, final int maximumBet, final boolean reshuffleAfterGame) {
        this.initialChips = initialChips;
        this.maximumBet = maximumBet;
        this.reshuffleAfterGame = reshuffleAfterGame;
    }

    public int getInitialChips() {
        return initialChips;
    }

    public int getMaximumBet() {
        return maximumBet;
    }

    public boolean isReshuffleAfterGame() {
        return reshuffleAfterGame;
    }

    public int getVersion() {
        return 1;
    }
}
