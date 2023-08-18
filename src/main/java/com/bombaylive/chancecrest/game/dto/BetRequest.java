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
     * The amount placed as bet.
     */
    @Min(value = 0, message = "Bet must be greater than 0.")
    private double betAmount;

    /**
     * The chosen number for the bet.
     */
    @Min(value = 1, message = "Chosen number should be greater than 1")
    @Max(value = 100, message = "Chosen number should be between 1 and 100")
    private int number;

}