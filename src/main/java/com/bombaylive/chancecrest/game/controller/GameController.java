package com.bombaylive.chancecrest.game.controller;

import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.game.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller for game functions.
 */
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    /**
     * Constructor-based dependency injection.
     *
     * @param gameService The game service bean.
     */
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Endpoint to make a bet.
     *
     * @param betRequest The bet request payload.
     * @return The bet response.
     */
    @PostMapping("/play")
    public BetResponse makeBet(@RequestBody @Valid BetRequest betRequest) {
        return gameService.play(betRequest);
    }
}
