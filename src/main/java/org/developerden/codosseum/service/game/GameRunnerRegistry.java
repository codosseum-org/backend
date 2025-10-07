package org.developerden.codosseum.service.game;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GameRunnerRegistry {
    private final GameRunnerFactory factory;
    private final Map<UUID, GameRunner> runners = new ConcurrentHashMap<>();

    public GameRunnerRegistry(GameRunnerFactory factory) {
        this.factory = factory;
    }

    public GameRunner getOrCreate(UUID gameId) {
        return runners.computeIfAbsent(gameId, factory::create);
    }


    public Optional<GameRunner> find(UUID gameId) {
        return Optional.ofNullable(runners.get(gameId));
    }

    public void stop(UUID gameId) {
        var r = runners.remove(gameId);
        if (r != null) r.shutdown();
    }
}
