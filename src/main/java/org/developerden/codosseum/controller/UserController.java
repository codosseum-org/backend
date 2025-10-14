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

package org.developerden.codosseum.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import org.developerden.codosseum.auth.GameAuthorized;
import org.developerden.codosseum.auth.GameRole;
import org.developerden.codosseum.dto.PlayersMapper;
import org.developerden.codosseum.dto.user.User;
import org.developerden.codosseum.model.player.UserMapper;
import org.developerden.codosseum.repository.AuthRepository;
import org.developerden.codosseum.service.AuthService;

@Validated
@Controller("/users")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UserController {
  private final AuthRepository authRepository;
  private final PlayersMapper playersMapper;

  private final AuthService authService;
  private final UserMapper userMapper;

  @Inject
  UserController(AuthRepository authRepository, PlayersMapper playersMapper,
                 AuthService authService, UserMapper userMapper) {
    this.authRepository = authRepository;
    this.playersMapper = playersMapper;
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @Get("/@self")
  @GameAuthorized(GameRole.PLAYER)
  public HttpResponse<User> getSelf(Authentication principal) {
    return authService.getUserInfoFromAuth(principal)
        .map(userMapper::toDto)
        .map(HttpResponse::ok)
        .orElse(HttpResponse.unauthorized());
  }
}
