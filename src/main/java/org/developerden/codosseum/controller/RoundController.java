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
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import java.security.Principal;
import org.developerden.codosseum.dto.PlayerRoundResult;
import org.developerden.codosseum.dto.Round;
import org.developerden.codosseum.model.Game;

@Controller("/games/{id}/rounds")
public class RoundController {

  @Get("/{round}")
  public HttpResponse<Round> getRound(
      Principal principal,
      @PathVariable("id") Game game,
      @PathVariable int round,
      @QueryValue(defaultValue = "false") boolean withCode
  ) {
    throw new UnsupportedOperationException();
  }

  @Get("/{round}/results/{player}")
  public HttpResponse<PlayerRoundResult> getRoundResults(
      @PathVariable("id") Game game,
      @PathVariable int round,
      @PathVariable String player
  ) {
    throw new UnsupportedOperationException();
  }

}
