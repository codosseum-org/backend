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

package org.developerden.codosseum.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import java.security.Principal;
import org.developerden.codosseum.dto.GameJoinResponse;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.model.Game;

@Validated
@Controller("/games/{id}/players")
public class PlayerController {

  @Get
  public HttpResponse<Players> getPlayers(@PathVariable("id") Game game) {
    throw new UnsupportedOperationException();
  }

  @Post
  public HttpResponse<GameJoinResponse> joinGame(
      @PathVariable("id") Game game,
      @Valid @Body Player player
  ) {
    throw new UnsupportedOperationException();
  }


  @Delete("/@self")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> leaveGame(Principal principal, @PathVariable("id") Game game) {
    throw new UnsupportedOperationException();
  }

}
