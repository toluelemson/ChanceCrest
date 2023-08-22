package com.bombaylive.chancecrest.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Data Transfer Object representing a bet request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetRequest {

    /**
     * A server generated random number.
     */
    private int serverRandomNumber;

    /**
     * The amount placed as bet.
     */
    @Min(value = 0, message = "Bet must be greater than 0.")
    private double betAmount;

    /**
     * The chosen number for the bet.
     */
    @Min(value = 1, message = "Chosen number should be greater than 1")
    @Max(value = 100, message = "Chosen number should be between 1 and 100")
    private int playerNumber;

    /**
     * The total number of players participating.
     */
    private int playerCount;

    /**
     * The number of threads to be used.
     */
    private int numberOfThreads;

    /**
     * Total money involved.
     */
    private double totalMoney;

    /**
     * Total number of betting rounds.
     */
    private int numberOfRounds;

    public BetRequest(double betAmount, int playerNumber) {

        this.betAmount = betAmount;
        this.playerNumber = playerNumber;
    }
}
