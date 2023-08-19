package com.bombaylive.chancecrest.game.controller;
import com.bombaylive.chancecrest.exception.InvalidBetAmountException;
import com.bombaylive.chancecrest.exception.InvalidNumberChoiceException;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for game functions.
 */
@RestController
public class GameController {

    private final SimpMessagingTemplate template;
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate template) {
        this.gameService = gameService;
        this.template = template;
    }

    /**
     * Endpoint to make a bet.
     *
     * @param betRequest The bet request payload.
     * @return The bet response.
     */
    @MessageMapping("/play")
    @SendTo("/topic/play")
    public ResponseEntity<BetResponse> makeBet(BetRequest betRequest) {
        try {
            gameService.validateBetRequest(betRequest);
            return ResponseEntity.ok(gameService.play(betRequest));
        } catch (InvalidBetAmountException | InvalidNumberChoiceException ex) {
            sendErrorMessage(ex.getMessage());
            return ResponseEntity.ok(new BetResponse(0));
        }
    }

    private void sendErrorMessage(String message) {
        template.convertAndSend("/topic/errors", message);
    }
}
