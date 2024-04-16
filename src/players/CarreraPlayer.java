package players;

import game.Card;
import game.HandRanks;
import game.Player;

public class CarreraPlayer extends Player {
    boolean bluff = false; //bluff is a boolean that is used as a way for the bot to make various actions that stray from the intended path
    public CarreraPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        bluff = (int)(Math.random() * 10 + 1) == 10;  //Bluff is set to true or false every turn, 10% for it to become true
        if (shouldAllIn()){ //Check to all-in first
            allIn();
        } else if (shouldFold()) {
            fold();
        } else if (shouldCheck()) {
            check();
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()){
            raise(evalRaiseValue());
        }

    }

    @Override
    protected boolean shouldFold() {
        if (getGameState().isActiveBet()) { //Checks if there is an active bet
            int roundStage = getGameState().getNumRoundStage();
            if (roundStage == 0) { // Check if the round stage is Pre-Flop
                if (getGameState().getTableBet() > getBank() / 2 && evaluatePlayerHand() == HandRanks.HIGH_CARD) { // Will fold if the bet is greater than half the bot's bank and the bot has only High-Card
                    return true;
                } else {
                    return false;
                }
            } else if (roundStage == 1) { // Check if the round stage is Flop
                if (getGameState().getTableBet() > (getBank() / 4) && evaluatePlayerHand() == HandRanks.HIGH_CARD) { //Will fold in Flop if there is a Bet higher than the players bank and when the player only has a High Card.
                    return true;
                } else {
                    return false;
                }
            } else if (roundStage == 2) { // Check if the round stage is turn
                if(getGameState().getTableBet() > getBank() / 6 && evaluatePlayerHand() == HandRanks.HIGH_CARD) { //If hand rank continues to be high card and table Bet is 1/6 of the bot's bank it will fold.
                    return true;
                } else {
                    return false;
                }
            } else if (roundStage == 3) {//Check if the round stage is river
                if (bluff && evaluatePlayerHand().getValue() <= 2) { //10% chance for the bot to bluff, and not fold in river with a pair or high card;
                    return false;
                } else if (evaluatePlayerHand() == HandRanks.HIGH_CARD) { //If the bot does not bluff than it will fold if it has high card in river
                    return true;
                } else if (getGameState().getTableBet() > getBank() / 3 && evaluatePlayerHand() == HandRanks.PAIR){ //Will fold if the bet is larger than a third of its bank while it has a pair.
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false; //if there is no active bet it will never fold.
    }

    @Override
    protected boolean shouldCheck() {
        if (!getGameState().isActiveBet()){
            int roundStage = getGameState().getNumRoundStage();
            if (roundStage == 0) { // Check if the round stage is Pre-Flop
                if (evaluatePlayerHand() == HandRanks.PAIR){ //Will not check, but rather it will aim to raise
                    return false;
                } else { //Otherwise it will check
                    return true;
                }
            } else if (roundStage == 1) { // Check if the round stage is Flop
                if (evaluatePlayerHand().getValue() >= 3){ //If the bot has a hand rank of TWO-PAIR or over it will aim to raise instead of checking;
                    return false;
                } else if (bluff){ //If the bot can bluff it will aim to raise, instead of normally checking
                    return false;
                } else {
                    return true;
                }
            } else if (roundStage == 2) { // Check if the round stage is Turn
                if (evaluatePlayerHand().getValue() >= 3){ //If the bot has a hand rank of TWO-PAIR or over it will aim to raise instead of checking;
                    return false;
                } else {
                    return true;
                }
            } else if (roundStage == 3) { // Check if the round stage is River
                if (evaluatePlayerHand().getValue() >= 3){ //If the bot has a hand rank of TWO-PAIR or over it will aim to raise instead of checking;
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false; //Should not check if there is a bet
    }

    @Override
    protected boolean shouldCall() {
        int roundStage = getGameState().getNumRoundStage();

        if (roundStage == 1 && bluff) { //to make sure the bluff from ShouldCheck, turns the bot's action into a raise
            return false;
        }

        if (getGameState().isActiveBet()) {
            if (roundStage == 0){ //Check if the stage is Pre-Flop
                if (evaluatePlayerHand() == HandRanks.PAIR) { // The bot should want to raise instead of check.
                    return false;
                } else { // only other hand rank is HIGH-CARD, it will call if the bet is not over half of the bot's bank.
                    return true;
                }
            } else if (roundStage == 1) { //Check if the stage is Flop
                if (evaluatePlayerHand().getValue() <= 3) { //Bot will aim to raise if it has a TWO-PAIR or higher
                    return false;
                } else { // On Hand Ranks HIGH-CARD and PAIR it will aim to call.
                    return true;
                }
            } else if (roundStage == 2) { //Check if the stage is Turn
                if (evaluatePlayerHand().getValue() <= 3) { //Bot will aim to raise if it has a TWO-PAIR or higher
                    return false;
                } else { // On Hand Ranks HIGH-CARD and PAIR it will aim to call.
                    return true;
                }
            } else if (roundStage == 3) { //Check if the stage is River
                if (evaluatePlayerHand().getValue() <= 3) { //Bot will aim to raise if it has a TWO-PAIR or higher
                    return false;
                } else if (bluff){ // This makes sure the bot raises when bluff is active, it should have folded instead.
                    return false;
                } else { //It will call on hand ranks HIGH CARD and PAIR when there is no bluff
                    return true;
                }
            }
        }
        return false; //Cannot Call when there is no active bet.
    }

    @Override
    protected boolean shouldRaise() {
        int roundStage = getGameState().getNumRoundStage();
        if (roundStage == 0){ //Check if the stage is Pre-Flop
            if (evaluatePlayerHand() == HandRanks.PAIR) { //Only two possibilities it will raise if it has a Pair in pre-flop
                return true;
            } else { //otherwise it will not.
                return false;
            }
        } else if (roundStage == 1) { //Check if the stage is Flop
            if (evaluatePlayerHand().getValue() >= 3) { //does one final check to see if HandRank is over TWO-PAIR, and asks it to raise.
                return true;
            } else if (bluff) { //Bluff from shouldCheck method makes it raise despite HandRank.
                return true;
            } else {
                return false;
            }
        } else if (roundStage == 2) { //Check if the stage is Turn
            if (evaluatePlayerHand().getValue() >= 3) { //does one final check to see if HandRank is over TWO-PAIR, and asks it to raise.
                return true;
            } else { //otherwise it will not.
                return false;
            }
        } else if (roundStage == 3) { //Check if the stage is River
            if (evaluatePlayerHand().getValue() >= 3) { //does one final check to see if HandRank is over TWO-PAIR, and asks it to raise.
                return true;
            } else if (bluff) { //Bluff from shouldFold method makes it raise despite HandRank.
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldAllIn() {
        int roundStage = getGameState().getNumRoundStage();
        if (roundStage == 0){ // Never want to all in on pre-flop;
            return false;
        } else if (roundStage == 1) { // Never want to all in on Flop;
            return false;
        } else if (roundStage == 2) { //Will only calculate if they should be all-in in turn or river
            if (evaluatePlayerHand().getValue() >= 6){ //Will All-In if I have a FLUSH or higher
                return true;
            } else if (isDealer() && bluff) { //Wild-Card Method for fun, Will All-In 10% of the time if I am the dealer,
                return true;
            } else { //If conditions are not met will not all-in
                return false;
            }
        } else if (roundStage == 3){ // Can all-in during a river
            if (evaluatePlayerHand().getValue() >= 5){ //Will All-In if I have a STRAIGHT or higher
                return true;
            } else { //If conditions are not met will not all-in
                return false;
            }
        }
        return false; //catch all
    }

    private int evalRaiseValue(){ //Method to check the amount the bot has to raise by
        int raiseVal = getBank();
        int handRank = evaluatePlayerHand().getValue();

        if (handRank == 1){ // setting raise multiplier depending on which Hand Rank you have
            raiseVal *= 0.1;
        } if (handRank == 2){
            raiseVal *= 0.2;
        } if (handRank == 3){
            raiseVal *= 0.3;
        } if (handRank == 4){
            raiseVal *= 0.4;
        } if (handRank == 5){
            raiseVal *= 0.5;
        } if (handRank == 6){
            raiseVal *= 0.6;
        } if (handRank == 7){
            raiseVal *= 0.7;
        }

        return raiseVal; //Takes the bot's bank multiplies it based on what hand rank it has, and returns it.
    }
}
