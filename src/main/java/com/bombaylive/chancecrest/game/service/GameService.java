package com.bombaylive.chancecrest.game.service;
import com.bombaylive.chancecrest.exception.InvalidBetAmountException;
import com.bombaylive.chancecrest.exception.InvalidNumberChoiceException;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service class for game operations.
 */
@Service
public class GameService {

    private final Random random = new Random();
    int serverNumber = random.nextInt(100) + 1;

    /**
     * Makes a bet based on the provided request.
     * @param betRequest The bet request.
     * @return The bet response.
     */
    public BetResponse play(BetRequest betRequest) {
        validateBetRequest(betRequest);
        double winValue = calculateWin(betRequest.getBetAmount(), betRequest.getNumber(), serverNumber);
        return new BetResponse(winValue);
    }

    /**
     * Validates the bet request to ensure data integrity.
     * @param betRequest The bet request.
     */
    private void validateBetRequest(BetRequest betRequest) {
        if (betRequest.getBetAmount() <= 0) {
            throw new InvalidBetAmountException("Bet amount should be greater than zero.");
        }

        int number = betRequest.getNumber();
        if (number < 1 || number > 100) {
            throw new InvalidNumberChoiceException("Chosen number should be between 1 and 100, inclusive.");
        }
    }

    /**
     * Calculates the win amount based on the bet and number.
     * @param bet The placed bet.
     * @param number The chosen number.
     * @return The calculated win amount.
     */
    private double calculateWin(double bet, int number, int serverNumber) {
        if (number > serverNumber) {
            return bet * (99.0 / (100.0 - number));
        }
        return 0.0;
    }
}