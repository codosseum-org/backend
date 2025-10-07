package org.developerden.codosseum.service.game;

import java.util.UUID;

/**
 * A command that can be executed on a {@link org.developerden.codosseum.model.Game}
 */
public sealed interface GameCommand {
    UUID gameId();

    /**
     * Command to start a game
     * @param gameId the id of the game to start
     */
    record StartGame(UUID gameId) implements GameCommand {
    }


}
