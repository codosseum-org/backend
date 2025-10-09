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

package org.developerden.codosseum.model.player;

import javax.annotation.Nonnull;

/**
 * An ephemeral player, not tied to any persistent identity.
 *
 * @param name  the name of the player.
 * @param key   a unique key for the player, used to identify them in the game.
 * @param admin whether the player is an admin - if this is true, the {@link #key} can be also used to authenticate admin actions.
 */
public record EphemeralPlayer(@Nonnull String name, @Nonnull String key, boolean admin)
    implements GamePlayer {
}
