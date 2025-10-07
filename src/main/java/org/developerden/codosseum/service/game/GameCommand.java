package org.developerden.codosseum.service.game;

import org.developerden.codosseum.dto.Player;

import java.util.UUID;

/**
 * A command that can be executed on a {@link org.developerden.codosseum.model.Game}
 */
public sealed interface GameCommand {
    UUID gameId();


    record CreateGame(UUID gameId) implements GameCommand {
    }

    /**
     * Command to start a game
     * @param gameId the id of the game to start
     */
    record StartGame(UUID gameId) implements GameCommand {
    }

    record AddPlayer(UUID gameId, Player player) implements GameCommand {
    }


}
