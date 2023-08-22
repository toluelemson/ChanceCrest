package com.bombaylive.chancecrest;

import com.bombaylive.chancecrest.game.controller.GameController;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.game.service.GameService;
import com.bombaylive.chancecrest.util.ServerNumberGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GameControllerTest {

    private static final double TEST_BET_AMOUNT = 40.5;
    private static final int TEST_PLAYER_NUMBER = 50;
    private static final int TEST_SERVER_NUMBER = ServerNumberGenerator.generate();
    private static final int PLAYER_COUNT = 1_000_000;
    private static final double EXPECTED_WIN_AMOUNT = 80.19;
    private static final int TEST_NUM_THREADS = 24;
    private static final int NUM_ROUNDS = 1_000_000;

    @Autowired
    private GameController gameController;

    @Autowired
    private GameService gameService;

    /**
     * Tests whether the makeBet method returns a non-null response.
     */
    @Test
    public void testMakeBet_returnsNonNullResponse() {
        BetRequest request = createTestBetRequest();
        ResponseEntity<BetResponse> response = gameController.makeBet(request);
        assertNotNull(response, "Response from makeBet should not be null.");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Expected a successful response status code from makeBet.");
    }

    /**
     * Tests whether the makeBet method returns the expected win amount.
     */
    @Test
    public void testMakeBet_returnsExpectedWinAmount() {
        BetRequest request = createTestBetRequest();
        ResponseEntity<BetResponse> response = gameController.makeBet(request);

        assertNotNull(response, "Response should not be null.");
        assertNotNull(response.getBody(), "Response body should not be null.");

        double actualWinAmount = response.getBody().getWinAmount();
        if (request.getPlayerNumber() > request.getServerRandomNumber()) {
            assertEquals(EXPECTED_WIN_AMOUNT, actualWinAmount, "Unexpected win amount returned.");
        } else {
            assertEquals(0.0, actualWinAmount, "Expected win amount of 0.0 when playerNumber >= serverRandomNumber.");
        }
    }

    /**
     * Tests the RTP calculation using streams.
     */
    @Test
    public void shouldReturnExpectedRTPUsingStream() {
        BetRequest request = createTestBetRequest();
        ResponseEntity<BetResponse> response = gameController.simulateRTP(request);

        assertNotNull(response, "Response should not be null.");
        assertNotNull(response.getBody(), "Response body should not be null.");

        double actualRTP = response.getBody().getWinAmount();
        assertTrue(actualRTP >= 0 && actualRTP <= 100.0, "RTP should be between 0% and 100%.");
    }

    /**
     * Tests the RTP calculation using threads.
     */
    @Test
    public void shouldReturnExpectedRTPUsingThreads() {
        BetRequest request = createTestBetRequest();
        double actualRTP = gameService.calculateRTPWithThreads(request);
        assertTrue(actualRTP >= 0 && actualRTP <= 100.0, "RTP should be between 0% and 100%.");
    }

    private BetRequest createTestBetRequest() {
        BetRequest request = new BetRequest();
        request.setBetAmount(TEST_BET_AMOUNT);
        request.setPlayerNumber(TEST_PLAYER_NUMBER);
        request.setPlayerCount(PLAYER_COUNT);
        request.setServerRandomNumber(TEST_SERVER_NUMBER);
        request.setNumberOfThreads(TEST_NUM_THREADS);
        request.setNumberOfRounds(NUM_ROUNDS);
        return request;
    }
}
