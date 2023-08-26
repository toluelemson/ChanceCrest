package com.bombaylive.chancecrest;

import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
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

        BetRequest request = new BetRequest(40.5, 50);
        session.send("/app/play", request);
        session.subscribe("/topic/play", sessionHandler);

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Response not received within timeout period");

        BetResponse response = betResponseRef.get();
        assertNotNull(response);
        assertEquals(80.19, response.getWinAmount());
    }
}
