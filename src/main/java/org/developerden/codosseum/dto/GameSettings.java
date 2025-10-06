/*
 * SPDX-FileCopyrightText: 2023 JohnnyJayJay
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.developerden.codosseum.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.developerden.codosseum.mode.GameModeType;

import java.util.List;


@Introspected
@RecordBuilder
@Serdeable
@Schema(
        description = "Settings for a Codosseum game"
)
public record GameSettings(
        // custom validator for elements
        @Nullable
        @Schema(description = "Which programming languages are allowed to be used for submissions. If omitted, all languages are allowed.")
        List<String> allowedLanguages,

        @Nullable
        @Schema(description = "Game modes from which the server will make a random selection. If omitted, all game modes are allowed")
        List<GameModeType> allowedGameModes,

        @Nullable
        @Min(2)
        @Max(50)
        @Schema(description = "Maximum player count for the game")
        Integer maxPlayers,

        @Nullable
        @Min(60)
        @Max(1800)
        @Schema(description = "Time limit for the game, in seconds.")
        Integer timeLimit,

        @Nullable
        @Min(0)
        @Max(300)
        @Schema(description = "Maximum warmup time for a game in seconds - that is, a timer that starts once 2 or more players have joined the game that delays the starting of the game to allow more players to join.")
        Integer maxWarmupTime
) {
}
