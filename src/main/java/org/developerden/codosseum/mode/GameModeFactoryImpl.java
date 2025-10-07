package org.developerden.codosseum.mode;

import jakarta.inject.Singleton;

@Singleton
public class GameModeFactoryImpl implements GameModeFactory {
    @Override
    public GameMode initForGame(String gameId) {
        return null;
    }

    @Override
    public GameMode fromType(GameModeType type) {
        return switch (type) {
            case FASTEST -> new FastestGameMode();
            default -> throw new UnsupportedOperationException("Unsupported game mode type: " + type);
        };
    }
}
