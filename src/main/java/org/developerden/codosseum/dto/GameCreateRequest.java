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
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Introspected
@Serdeable
@Schema(
        description = "Settings for the new game"
)
public record GameCreateRequest(
        @NotNull
        GameSettings settings,
        @NotNull
        @Schema(description = """
                Player info of the person creating the game.
                Without special authorisation, this must not be omitted and the provided
                player will join the newly created game automatically.
                
                In the future, there might be a mechanism for technical users to create games without joining them,
                where this property is optional.
                """)
        Player player
) {
}
