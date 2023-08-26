package com.bombaylive.chancecrest.game.controller;
import com.bombaylive.chancecrest.exception.InvalidBetAmountException;
import com.bombaylive.chancecrest.exception.InvalidNumberChoiceException;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/play")
    public ResponseEntity<BetResponse> makeBet(@RequestBody BetRequest betRequest) {
        try {
            BetResponse response = gameService.play(betRequest);
            template.convertAndSend("/topic/play", response);
            return ResponseEntity.ok(response);

        } catch (InvalidBetAmountException | InvalidNumberChoiceException ex) {
            return sendErrorMessage(ex.getMessage());
        }
    }


    @MessageMapping("/multiPlay")
    @PostMapping("/multiPlay")
    public ResponseEntity<BetResponse> simulateRTP(@RequestBody BetRequest betRequest) {
        BetResponse response = new BetResponse(gameService.calculateRTPWithStream(betRequest));
        template.convertAndSend("/topic/multiPlay", response);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<BetResponse> sendErrorMessage(String message) {
        template.convertAndSend("/topic/errors", message);
        return new ResponseEntity<>(new BetResponse(), HttpStatus.BAD_REQUEST);
    }
}
