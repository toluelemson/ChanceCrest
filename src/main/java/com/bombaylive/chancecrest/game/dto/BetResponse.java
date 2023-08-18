package com.bombaylive.chancecrest.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a bet response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetResponse {

    /**
     * The amount won from the bet.
     */
    private double winAmount;

}
