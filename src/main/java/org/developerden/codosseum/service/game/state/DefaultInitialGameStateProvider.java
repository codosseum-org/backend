package org.developerden.codosseum.service.game.state;

import jakarta.inject.Singleton;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;

import java.util.UUID;

/**
 * Default initial state: lobby waiting for players.
 */
@Singleton
public class DefaultInitialGameStateProvider implements InitialGameStateProvider {
    @Override
    public GameState create(UUID gameId) {
        return GameStateBuilder.builder()
                .gameId(gameId)
                .phase(GamePhase.WAITING_FOR_PLAYERS)
                .build();
    }
}