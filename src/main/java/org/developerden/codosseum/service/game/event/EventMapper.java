package org.developerden.codosseum.service.game.event;

import jakarta.inject.Singleton;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.event.PlayerJoinEvent;

import java.util.Optional;

@Singleton
public class EventMapper {

    public Optional<GameEvent> fromInternal(InternalGameEvent internalEvent) {
        return switch (internalEvent) {
            case InternalGameEvent.GameCreated ignored -> Optional.empty();

            case InternalGameEvent.PlayerJoined playerJoined -> Optional.of(
                    new PlayerJoinEvent(playerJoined.gameId(), playerJoined.player())
            );
        };
    }
}
