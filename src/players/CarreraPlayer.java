package players;

import game.Player;


public class CarreraPlayer extends Player {

    public CarreraPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        if (shouldFold()){
            fold();
        } else if (shouldCheck()) {
            check();
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()) {
            raise(10);
        } else if (shouldAllIn()) {
            allIn();
        }
    }

    @Override
    protected boolean shouldFold() {
        if (isBetActive()) {
            int roundStage = getGameState().getNumRoundStage();
            if (roundStage == 0) { // Check if the round stage is Pre-Flop
                int valueDifference = Math.abs(getHandCards().get(0).getValue() - getHandCards().get(1).getValue());
                if (valueDifference >= 4) { // Check if the difference in values is greater than or equal to 4
                    return true; // Fold if the difference in values is greater than 4
                }
            } else if (roundStage == 1) { // Check if the round stage is Flop
                if (evaluatePlayerHand().getValue() < 1 && getGameState().getTableBet() > 10){
                    return true;
                }
            }
        }
        return false; //if all doesn't work do not fold
    }

    @Override
    protected boolean shouldCheck() {
        return (!getGameState().isActiveBet());
    }

    @Override
    protected boolean shouldCall() {
        return (getGameState().isActiveBet());
    }

    @Override
    protected boolean shouldRaise() {
        return false;
    }

    @Override
    protected boolean shouldAllIn() {
        return false;
    }
}
