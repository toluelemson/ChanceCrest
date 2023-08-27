package com.bombaylive.chancecrest;

import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.util.ServerNumberGenerator;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;


    private static final double TEST_BET_AMOUNT = 40.5;
    private static final int TEST_PLAYER_NUMBER = 50;
    private static final int TEST_SERVER_NUMBER = ServerNumberGenerator.generate();
    private static final int TEST_NUM_THREADS = 24;
    private static final int NUM_ROUNDS = 1_000_000;
    private WebSocketStompClient stompClient;

    @Before
    public void setup() {
        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())));
        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testMakeBet() throws Exception {
        AtomicReference<BetResponse> betResponseRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            @NonNull
            public Type getPayloadType(@NonNull StompHeaders headers) {
                return BetResponse.class;
            }

            @Override
            public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                assertNotNull(payload);
                betResponseRef.set((BetResponse) payload);
                latch.countDown();
            }
        };

        StompSession session = this.stompClient.connect("ws://localhost:" + port + "/ws", sessionHandler).get(1, TimeUnit.SECONDS);

        session.send("/app/play", createTestBetRequest());
        session.subscribe("/topic/play", sessionHandler);

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Response not received within timeout period");

        BetResponse response = betResponseRef.get();
        assertNotNull(response);
        double actualWinAmount = response.getWinAmount();
        if (createTestBetRequest().getPlayerNumber() > createTestBetRequest().getServerRandomNumber()) {
            assertEquals(80.19, actualWinAmount, "Unexpected win amount returned.");
        } else {
            assertEquals(0.0, actualWinAmount, "Expected win amount of 0.0 when playerNumber >= serverRandomNumber.");
        }

        session.disconnect();
    }


    @Test
    public void testMultiPlayerBet() throws Exception {
        AtomicReference<BetResponse> betResponseRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            @NonNull
            public Type getPayloadType(@NonNull StompHeaders headers) {
                return BetResponse.class;
            }

            @Override
            public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                assertNotNull(payload);
                betResponseRef.set((BetResponse) payload);
                latch.countDown();
            }
        };

        StompSession session = this.stompClient.connect("ws://localhost:" + port + "/ws", sessionHandler).get(1, TimeUnit.SECONDS);

        session.send("/app/multiPlay", createTestBetRequest());
        session.subscribe("/topic/multiPlay", sessionHandler);

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Response not received within timeout period");

        BetResponse response = betResponseRef.get();
        double rtpValue = response.getWinAmount();
        assertTrue(rtpValue >= 0 && rtpValue <= 100.0, "RTP should be between 0% and 100%.");
        System.out.println(response);

        session.disconnect();
    }

    private BetRequest createTestBetRequest() {
        BetRequest request = new BetRequest();
        request.setBetAmount(TEST_BET_AMOUNT);
        request.setPlayerNumber(TEST_PLAYER_NUMBER);
        request.setServerRandomNumber(TEST_SERVER_NUMBER);
        request.setNumberOfThreads(TEST_NUM_THREADS);
        request.setNumberOfRounds(NUM_ROUNDS);
        return request;
    }
}
