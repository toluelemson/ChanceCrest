package com.bombaylive.chancecrest.game.service;

import com.bombaylive.chancecrest.exception.InvalidBetAmountException;
import com.bombaylive.chancecrest.exception.InvalidNumberChoiceException;
import com.bombaylive.chancecrest.game.dto.BetRequest;
import com.bombaylive.chancecrest.game.dto.BetResponse;
import com.bombaylive.chancecrest.util.ServerNumberGenerator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service class for game operations.
 */
@Service
@Slf4j
public class GameService {
    private final SimpMessagingTemplate template;

    @Autowired
    public GameService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public BetResponse play(BetRequest betRequest) {
        ensureValidServerNumber(betRequest);
        validateBetRequest(betRequest);

        double winAmount = calculateWin(betRequest);
        template.convertAndSend("/topic/play", winAmount);

        return BetResponse.builder()
                .winAmount(winAmount)
                .build();
    }

    private void ensureValidServerNumber(BetRequest betRequest) {
        int serverNumber = betRequest.getServerRandomNumber() > 0 ?
                betRequest.getServerRandomNumber() :
                ServerNumberGenerator.generate();
        betRequest.setServerRandomNumber(serverNumber);
    }

    private void validateBetRequest(BetRequest betRequest) {
        double betAmount = betRequest.getBetAmount();
        if (betAmount < 1 || betAmount > 100) {
            throw new InvalidBetAmountException("Bet amount should be between 1 and 100.");
        }

        int number = betRequest.getPlayerNumber();
        if (number < 1 || number > 100) {
            throw new InvalidNumberChoiceException("Chosen number should be between 1 and 100");
        }
    }
    private double calculateWin(BetRequest betRequest) {
        if (betRequest.getServerRandomNumber() < betRequest.getPlayerNumber()) {
            double winMultiplier = 99.0 / (100 - betRequest.getPlayerNumber());
            return betRequest.getBetAmount() * winMultiplier;
        }
        return 0;
    }

    public double calculateRTPWithStream(BetRequest betRequest) {
        ensureValidServerNumber(betRequest);
        validateBetRequest(betRequest);
        DoubleAdder totalSpent = new DoubleAdder();
        DoubleAdder totalWon = new DoubleAdder();

        IntStream.range(0, betRequest.getNumberOfRounds())
                .parallel()
                .forEach(i -> playGameRound(totalSpent, totalWon, betRequest));

        return computeRTP(totalWon, totalSpent);
    }

    public double calculateRTPWithThreads(BetRequest betRequest) {
        ensureValidServerNumber(betRequest);
        validateBetRequest(betRequest);
        DoubleAdder totalSpent = new DoubleAdder();
        DoubleAdder totalWon = new DoubleAdder();

        List<Callable<Void>> tasks = IntStream.range(0, betRequest.getNumberOfRounds())
                .mapToObj(i -> createGameRoundTask(totalSpent, totalWon, betRequest))
                .collect(Collectors.toList());

        executeTasksWithThreadPool(tasks, betRequest.getNumberOfThreads());

        return computeRTP(totalWon, totalSpent);
    }

    private Callable<Void> createGameRoundTask(DoubleAdder totalSpent, DoubleAdder totalWon, BetRequest betRequest) {
        return () -> {
            playGameRound(totalSpent, totalWon, betRequest);
            return null;
        };
    }

    private void executeTasksWithThreadPool(List<Callable<Void>> tasks, int numberOfThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Thread interrupted during execution", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            shutdownExecutor(executor);
        }
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Executor termination interrupted", e);
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }

    private double computeRTP(DoubleAdder totalWon, DoubleAdder totalSpent) {
        return (totalWon.sum() / totalSpent.sum()) * 100;
    }

    private void playGameRound(DoubleAdder totalSpent, DoubleAdder totalWon, BetRequest betRequest) {
        BetRequest br = BetRequest.builder()
                .betAmount(betRequest.getBetAmount())
                .playerNumber(betRequest.getPlayerNumber())
                .build();

        totalSpent.add(br.getBetAmount());

        BetResponse response = play(br);
        totalWon.add(response.getWinAmount());
    }
}
