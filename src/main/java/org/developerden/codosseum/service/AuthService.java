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

package org.developerden.codosseum.service;

import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.auth.PlayerAuthentication;
import org.developerden.codosseum.model.player.CodosseumUser;
import org.developerden.codosseum.model.player.RegisteredUser;
import org.developerden.codosseum.repository.AuthRepository;

@Singleton
public class AuthService {
  private final AuthRepository authRepository;

  public AuthService(AuthRepository authRepository) {
    this.authRepository = authRepository;
  }

  public Optional<CodosseumUser> getUserInfoFromAuth(Authentication authentication) {
    if (authentication.getAttributes().containsKey(PlayerAuthentication.ACTIVE_GAME_ID)) {
      // it's a GameKey auth
      return authRepository.findPlayerByNameAndGameId(
              authentication.getName(),
              (UUID) authentication.getAttributes().get(PlayerAuthentication.ACTIVE_GAME_ID))
          .map(x -> x); // lol
    }
    // otherwise assume it's an OAuth user
    UUID uuid;
    try {
      uuid = UUID.fromString(authentication.getName());
    } catch (Exception e) {
      return Optional.empty();
    }
    return authRepository.findRegisteredUserById(uuid)
        .or(() -> {
          var user = new RegisteredUser(
              uuid,
              (String) authentication.getAttributes().get("iss"),
              (String) authentication.getAttributes().get("sub"),
              (String) authentication.getAttributes().get("name")
          );
          authRepository.saveRegisteredUser(user);
          return Optional.of(user);
        })
        .map(x -> (CodosseumUser) x);
  }
}
