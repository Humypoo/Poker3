package players;

import game.HandRanks;
import game.Player;

public class ConservativeNPCPlayer extends Player {

    public ConservativeNPCPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        // This method would be called to initiate the NPC's decision-making process for its turn.
        // For simplicity, we're directly calling the decision methods based on the game state.
        if (shouldFold()) {
            fold();
        } else if (shouldCheck()) {
            check();
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()) {
            raise(getGameState().getTableMinBet()); // Example: Always raises with the table's minimum bet.
        } else if (shouldAllIn()) {
            allIn();
        }
    }
////
    @Override
    protected boolean shouldFold() {
        // Example logic: Fold if the hand rank is below a certain threshold.
        HandRanks handRank = evaluatePlayerHand();
        return handRank.compareTo(HandRanks.PAIR) < 0;
    }

    @Override
    protected boolean shouldCheck() {
        // Example logic: Check if no active bet and hand is not strong.
        return !getGameState().isActiveBet() && evaluatePlayerHand().compareTo(HandRanks.TWO_PAIR) < 0;
    }

    @Override
    protected boolean shouldCall() {
        // Example logic: Call if the hand is decent and there's an active bet.
        return getGameState().isActiveBet() && evaluatePlayerHand().compareTo(HandRanks.TWO_PAIR) >= 0;
    }

    @Override
    protected boolean shouldRaise() {
        // Example logic: Raise if the hand is strong.
        return evaluatePlayerHand().compareTo(HandRanks.THREE_OF_A_KIND) >= 0;
    }

    @Override
    protected boolean shouldAllIn() {
        // Example logic: Go all-in if the hand is very strong.
        return evaluatePlayerHand().compareTo(HandRanks.FOUR_OF_A_KIND) >= 0;
    }
}
