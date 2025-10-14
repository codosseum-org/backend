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

package org.developerden.codosseum.auth;

import io.micronaut.security.authentication.Authentication;
import jakarta.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.developerden.codosseum.model.player.EphemeralPlayer;

public final class PlayerAuthentication {

  public static final String ACTIVE_GAME_ID = "activeGameId";

  public static Authentication build(
      @Nonnull String name, @Nonnull UUID activeGameId, @Nonnull Set<GameRole> roles) {
    return Authentication.build(
        name,
        roles.stream().map(Enum::name).collect(Collectors.toSet()),
        Map.of(ACTIVE_GAME_ID, activeGameId)
    );
  }

  public static Authentication buildFrom(EphemeralPlayer ephemeralPlayer) {
    return build(
        ephemeralPlayer.name(),
        ephemeralPlayer.gameId(),
        ephemeralPlayer.admin() ? EnumSet.of(GameRole.ADMIN, GameRole.PLAYER) : EnumSet.of(GameRole.PLAYER)
    );
  }

}
