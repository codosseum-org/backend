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

/**
 * Internal representation of a <i>user</i>, as in, someone with some information registered on the system.
 * This can be either a {@link RegisteredUser}, who has an account, or an {@link EphemeralPlayer}, who does not.
 *
 * @apiNote This is distinct from a {@link GamePlayer}, which is a user in the context of a specific game.
 */
public sealed interface CodosseumUser permits EphemeralPlayer, RegisteredUser {
}
