package org.developerden.codosseum.service.game.event;

import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.event.PlayerJoinEvent;
import org.developerden.codosseum.model.GamePlayers;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.GamePlayer;

import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class EventMapper {

    public Optional<GameEvent> fromInternal(InternalGameEvent internalEvent) {
        return switch (internalEvent) {
            case InternalGameEvent.GameCreated ignored -> Optional.empty();

            case InternalGameEvent.PlayerJoined playerJoined -> Optional.of(
                    new PlayerJoinEvent(playerJoined.gameId(), fromGamePlayer(playerJoined.player()))
            );
        };
    }

    private Optional<Players> fromGamePlayers(@Nonnull GamePlayers gamePlayers) {
        var players = new Players(
                gamePlayers.others().stream()
                        .map(this::fromGamePlayer)
                        .collect(Collectors.toSet()),
                fromGamePlayer(gamePlayers.admin())
        );
        return Optional.of(players);
    }

    private Player fromGamePlayer(@Nonnull GamePlayer player) {
        return switch (player) {
            case EphemeralPlayer(var name, var ignored, var ignored2) -> (new Player(name));
        };
    }
}
