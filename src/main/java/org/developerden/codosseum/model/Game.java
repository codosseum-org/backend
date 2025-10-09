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

package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.UUID;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.mode.GameMode;

/**
 * Internal model representing the immutable <i>only</i> data about a game.
 * All mutable data is held in {@link GameState}.
 *
 * @param id       the unique identifier of the game.
 * @param adminKey the admin key for the game, used to authenticate admin actions.
 * @param settings the settings for the game.
 * @param mode     the game mode.
 */
@RecordBuilder
public record Game(
    UUID id,
    // TODO: this should be removed and handled via players' individual keys + {@link EphemeralPlayer#admin()}
    @Deprecated String adminKey,
    GameSettings settings,
    GameMode mode
) {
}
