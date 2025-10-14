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

package org.developerden.codosseum.dto.user;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.UUID;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.model.player.CodosseumUser;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.RegisteredUser;

/**
 * DTO for a user, with minimal game-specific information.
 *
 * @param id     the unique identifier of the user, or null if the user is not a {@link RegisteredUser}.
 * @param name   the display name of the user.
 * @param gameId the unique identifier of the game the user is registered in, if the user is a {@link EphemeralPlayer}, null otherwise
 * @see CodosseumUser the internal representation of this type
 * @see Player the game-specific DTO representation of a user
 */
@Serdeable
public record User(
    @Nullable UUID id,
    @Nonnull String name,
    @Nullable UUID gameId
) {
}
