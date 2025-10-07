package org.developerden.codosseum.service.game;

import org.developerden.codosseum.model.Game;

import java.util.UUID;

/// An event that happened to a [Game]
/// See also [GameCommand]
public sealed interface GameEvent {
    UUID gameId();

    record GameStarted(UUID gameId) implements GameEvent {
    }
}
