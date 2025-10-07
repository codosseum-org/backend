package org.developerden.codosseum.service.game;

import org.developerden.codosseum.model.GameState;

import java.util.UUID;

public interface GameAggregateFactory {
    GameAggregate create(UUID gameId, GameState snapshot);
}