package com.bombaylive.chancecrest;

import com.bombaylive.chancecrest.game.controller.GameController;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameControllerTest {

    private static final double TEST_BET_AMOUNT = 40.5;
    private static final int TEST_NUMBER = 50;
    private static final double EXPECTED_WIN_AMOUNT = 80.19;

    @Autowired
    private GameController gameController;

    @Test
    public void testMakeBet_returnsNonNullResponse() {
        BetRequest request = createTestBetRequest();

        ResponseEntity<BetResponse> response = gameController.makeBet(request);

        assertTrue(response.getStatusCode().is2xxSuccessful(), "Expected a successful response status code from makeBet.");
    }

    @Test
    public void testMakeBet_returnsExpectedWinAmount() {
        BetRequest request = createTestBetRequest();

        ResponseEntity<BetResponse> response = gameController.makeBet(request);

        assertEquals(EXPECTED_WIN_AMOUNT, Objects.requireNonNull(response.getBody()).getWinAmount(), "Expected win amount of " + EXPECTED_WIN_AMOUNT + ", but received a different amount");
    }

    private BetRequest createTestBetRequest() {
        BetRequest request = new BetRequest();
        request.setBetAmount(TEST_BET_AMOUNT);
        request.setNumber(TEST_NUMBER);
        return request;
    }
}