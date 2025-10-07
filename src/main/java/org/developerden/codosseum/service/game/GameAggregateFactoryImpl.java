package org.developerden.codosseum.service.game;

import jakarta.inject.Singleton;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.GameState;

import java.util.UUID;

@Singleton
public class GameAggregateFactoryImpl implements GameAggregateFactory {
    @Override
    public GameAggregate create(UUID gameId, GameState snapshot) {
        return new GameAggregate(gameId, snapshot);
    }
}
