package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.model.player.GamePlayer;

import java.util.UUID;

/**
 * Internal model representing the current state of a game.
 * @param gameId the unique identifier of the game
 * @param phase the current phase of the game
 * @param players the players involved in the game
 */
@RecordBuilder
public record GameState(UUID gameId,
                        GamePhase phase,
                        GamePlayers players
                        ) {
}
