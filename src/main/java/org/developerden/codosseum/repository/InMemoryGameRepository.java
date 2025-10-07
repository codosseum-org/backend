package org.developerden.codosseum.repository;

import jakarta.inject.Singleton;
import org.developerden.codosseum.model.Game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryGameRepository implements GameRepository {
    private final Map<UUID, Game> games = new ConcurrentHashMap<>();

    @Override
    public Game findGameById(UUID id) {
        return games.get(id);
    }

    @Override
    public void insertGame(Game game) {
        games.put(game.id(), game);
    }
}
