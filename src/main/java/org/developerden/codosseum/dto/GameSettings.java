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
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.developerden.codosseum.mode.GameMode;


@Introspected
@RecordBuilder
public record GameSettings(
    // custom validator for elements
    @Nullable
    List<String> allowedLanguages,

    @Nullable
    GameMode gameMode,

    @Nullable
    @Min(2)
    @Max(50)
    Integer maxPlayers,

    @Nullable
    @Min(60)
    @Max(1800)
    Integer timeLimit,

    @Nullable
    @Min(0)
    @Max(300)
    Integer maxWarmupTime
) {
}
