package org.developerden.codosseum.event;

import org.developerden.codosseum.dto.Player;

import java.util.UUID;

public record PlayerJoinEvent(UUID gameId, Player player) implements GameEvent {
}
