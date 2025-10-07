package org.developerden.codosseum.service.game.event;

import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.service.game.GameCommand;

import java.util.UUID;

/// An event that happened to a [Game]
/// This is the internal version of [GameEvent], and is used for internal messaging.
///
/// Some events may not have a public counterpart, see [EventMapper]
///
/// See also [GameCommand]
public sealed interface InternalGameEvent {
    UUID gameId();

    ///  Emitted when a new game is created and the lobby is opened
    record GameCreated(UUID gameId) implements InternalGameEvent {
    }

    record PlayerJoined(UUID gameId, Player player) implements InternalGameEvent {
    }
}
