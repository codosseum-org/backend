package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.developerden.codosseum.model.player.GamePlayer;

import java.util.Set;

/**
 * Internal model representing the players in a game.
 *
 * @param admin  the admin player
 * @param others the other players
 */
@RecordBuilder()
@RecordBuilder.Options(
        useImmutableCollections = true,
        addSingleItemCollectionBuilders = true
)
public record GamePlayers(GamePlayer admin, Set<GamePlayer> others) {
}
