package com.bombaylive.chancecrest.game.service;

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
    private final int serverNumber = random.nextInt(100) + 1;

    /**
     * Makes a bet based on the provided request.
     * @param betRequest The bet request.
     * @return The bet response.
     */
    public BetResponse play(BetRequest betRequest) {
        double winValue = calculateWin(betRequest.getBetAmount(), betRequest.getNumber());
        return new BetResponse(winValue);
    }

    /**
     * Calculates the win amount based on the bet and number.
     * @param bet The placed bet.
     * @param number The chosen number.
     * @return The calculated win amount.
     */
    private double calculateWin(double bet, int number) {
        if (number > serverNumber) {
            return bet * (99.0 / (100.0 - number));
        }
        return 0.0;
    }
}