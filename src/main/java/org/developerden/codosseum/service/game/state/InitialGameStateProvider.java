package org.developerden.codosseum.service.game.state;

import org.developerden.codosseum.model.GameState;

import java.util.UUID;

/**
 * Produces the initial GameState for a given game id.
 */
@FunctionalInterface
public interface InitialGameStateProvider {
    GameState create(UUID gameId);
}