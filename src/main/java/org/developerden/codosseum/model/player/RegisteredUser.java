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

import java.util.UUID;

/**
 * Registered user linked to an OAuth/OIDC identity.
 *
 * @param id          the unique identifier of the user.
 * @param provider    the identity provider (e.g., "google", "github").
 * @param subject     the subject identifier from the identity provider.
 * @param displayName the display name of the user.
 */
public record RegisteredUser(UUID id,
                             String provider,
                             String subject,
                             String displayName) implements CodosseumUser {
}
