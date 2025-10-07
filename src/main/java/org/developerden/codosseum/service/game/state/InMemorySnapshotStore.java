package org.developerden.codosseum.service.game.state;

import jakarta.inject.Singleton;
import org.developerden.codosseum.model.GameState;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@Singleton
public final class InMemorySnapshotStore implements SnapshotStore {
    private final ConcurrentMap<UUID, GameState> store = new ConcurrentHashMap<>();
    private final Function<UUID, GameState> initialProvider;

    public InMemorySnapshotStore(Function<UUID, GameState> initialProvider) {
        this.initialProvider = initialProvider;
    }

    @Override
    public Optional<GameState> load(UUID gameId) {
        return Optional.ofNullable(store.get(gameId));
    }

    @Override
    public void save(UUID gameId, GameState state) {
        store.put(gameId, state);
    }

    @Override
    public GameState createInitial(UUID gameId) {
        var state = initialProvider.apply(gameId);
        store.put(gameId, state);
        return state;
    }
}