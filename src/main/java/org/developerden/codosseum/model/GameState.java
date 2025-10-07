package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.UUID;

@RecordBuilder
public record GameState(UUID gameId,
                        GamePhase phase
                        ) {
}
