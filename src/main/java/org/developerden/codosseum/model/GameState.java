package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.developerden.codosseum.dto.Players;

import java.util.UUID;

@RecordBuilder
public record GameState(UUID gameId,
                        GamePhase phase,
                        Players players
                        ) {
}
