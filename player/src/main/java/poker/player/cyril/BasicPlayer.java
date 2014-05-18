package poker.player.cyril;

import java.util.concurrent.ThreadLocalRandom;

import poker.MoveDTO;
import poker.Player;
import poker.PlayerStateDTO;
import poker.TableDTO;
import poker.TableEvent;
import poker.TableListener;
import poker.TerminationEvent;
import poker.TerminationListener;

/**
 * Basic Player Implementation
 * 
 * Aim of this class : - Be as simple as possible but still a valid player -
 * Validate test case with the set of minimal constraints - Discover the Poker
 * API
 **/
public class BasicPlayer implements Player, TableListener, TerminationListener {
    
    public BasicPlayer() {
    }

    public BasicPlayer(String string) {
    }

    @Override
    public void terminatedBy(TerminationEvent event) {
    }

    @Override
    public void tableChanged(TableEvent event) {
    }

    @Override
    public MoveDTO makeMove(PlayerStateDTO pi, TableDTO table) {
        if(table.getChipsToCall() < pi.getChipsLeft()) {
            return MoveDTO.CALL;
        }
        return MoveDTO.FOLD;
    }
    
}