/*
 * # SPDX-FileCopyrightText: 2025 Alexander Wood (BristerMitten)
 * # SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.developerden.codosseum.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import org.developerden.codosseum.mode.GameMode;
import org.developerden.codosseum.model.GamePhase;

/**
 * Public information about a game.
 *
 * @param timeLeft A generic countdown timer, in seconds. Meaning depends on the game phase:
 *                 <ul>
 *                 <li>{@link GamePhase#WARMUP}: Time until game starts</li>
 *                 <li>{@link GamePhase#IN_PROGRESS}: Time until current round ends</li>
 *                 <li>Other phases are undefined and the value should be ignored</li>
 *                 </ul>
 */
@RecordBuilder
@Serdeable
@Schema(
    description = "Public information about a game",
    title = "GameInfo"
)
public record GameInfo(
    @Nonnull
    GameSettings settings,

    @Nonnull
    UUID id,

    @Nonnull
    GameMode gameMode,

    @Nonnull
    Players players,

    @Nonnull
    GamePhase state,

    @Schema(
        description = """
            A generic countdown timer, in seconds. Meaning depends on the game phase:
            - *WARMUP*: Time until game starts
            - *IN_PROGRESS*: Time until current round ends
            - Other phases are undefined and the value should be ignored
            """,
        example = "120",
        minimum = "0"
    )
    int timeLeft,

    @Schema(
        description = "The current round number, or null if the game hasn't started yet",
        example = "1",
        minimum = "1"
    )
    @Nullable
    Integer round,

    @Nonnull
    List<PlayerGameResult> results
) {
}
