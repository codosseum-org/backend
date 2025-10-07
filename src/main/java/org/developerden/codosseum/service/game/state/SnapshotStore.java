package org.developerden.codosseum.service.game.state;

import org.developerden.codosseum.model.GameState;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence boundary for GameState snapshots.
 * Pure IO: load/save by gameId. No domain logic or timers here.
 */
public interface SnapshotStore {
    Optional<GameState> load(UUID gameId);
    void save(UUID gameId, GameState state);
    GameState createInitial(UUID gameId);
}