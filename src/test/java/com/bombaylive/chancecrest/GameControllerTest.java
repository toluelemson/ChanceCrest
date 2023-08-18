package com.bombaylive.chancecrest;
import com.bombaylive.chancecrest.game.controller.GameController;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GameControllerTest {

    @Autowired
    private GameController gameController;

    /**
     * Tests the makeBet method in GameController for expected win amount.
     */

    @Test
    public void whenMakingABet_thenResponseShouldNotBeNull() {
        BetRequest request = new BetRequest();
        request.setBetAmount(40.5);
        request.setNumber(50);

        BetResponse response = gameController.makeBet(request);

        assertNotNull(response, "Expected a valid response from makeBet but got null");
    }

    @Test
    public void givenBetRequest_whenMakingABet_thenWinAmountShouldMatchExpectation() {
        BetRequest request = new BetRequest();
        request.setBetAmount(40.5);
        request.setNumber(50);

        BetResponse response = gameController.makeBet(request);

        assertEquals(80.19, response.getWinAmount(), "Expected win amount of 80.19, but received a different amount");
    }
}