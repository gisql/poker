/*
 * Copyright
 */
package pl.gsiql.poker;

/**
 * TODO describe me!
 *
 * @version created on 2014-04-17, 11:25
 */
public class MoveDTO {
    public static final MoveDTO CALL = new MoveDTO(0, MoveType.CALL);
    public static final MoveDTO FOLD = new MoveDTO(0, MoveType.FOLD);
    public static final MoveDTO ALL_IN = new MoveDTO(0, MoveType.ALL_IN);

    private final MoveType type;
    private final int chips;

    private MoveDTO(final int chips, final MoveType type) {
        this.chips = chips;
        this.type = type;
    }

    public static MoveDTO raise(final int chips) {
        return new MoveDTO(chips, MoveType.RAISE);
    }

    public static enum MoveType {
        ALL_IN,
        RAISE,
        CALL,
        FOLD
    }

    public int getChips() {
        return chips;
    }

    public MoveType getType() {
        return type;
    }
}
