package org.developerden.codosseum.service.game;

import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;

import java.util.List;
import java.util.UUID;

public class GameAggregate {
    private final UUID gameId;

    private final GameState gameState;

    public GameAggregate(UUID gameId, GameState gameState) {
        this.gameId = gameId;
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Result handle(GameCommand cmd) {
        var events = decide(cmd);
        var newState = applyAll(gameState, events);

        var next = new GameAggregate(gameId, newState);

        return new Result(events, next);
    }

    private List<GameEvent> decide(GameCommand cmd) {
        if (!cmd.gameId().equals(gameId)) {
            throw new IllegalArgumentException("Command gameId does not match aggregate gameId");
        }

        return switch (cmd) {
            case GameCommand.StartGame(var id) -> {
                if (gameState.phase() != GamePhase.WAITING_FOR_PLAYERS) {
                    throw new IllegalStateException("Cannot start game that is not in WAITING_FOR_PLAYERS phase");
                } else {
                    // TODO check if enough players
                    yield List.of(new GameEvent.GameStarted(id));
                }
            }

        };
    }

    private  GameState applyAll(GameState state, List<GameEvent> events) {
        var newState = state;
        for (var event : events) {
            newState = apply(newState, event);
        }
        return newState;
    }

    private  GameState apply(GameState state, GameEvent event) {
        return switch (event) {
            case GameEvent.GameStarted(var id) -> GameStateBuilder.builder(state).phase(GamePhase.WARMUP).build();
        };

    }


    public record Result(List<GameEvent> events, GameAggregate next) {
    }

}
