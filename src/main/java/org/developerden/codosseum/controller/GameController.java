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
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.sse.Event;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.security.Principal;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.model.Game;
import org.reactivestreams.Publisher;

@Validated
@Controller("/games")
public class GameController {

  @Post
  public HttpResponse<GameCreateResponse> createGame(@Valid @Body GameSettings settings) {
    throw new UnsupportedOperationException();
  }

  // typed argument binding for looking up games
  @Get("/{id}")
  public HttpResponse<GameInfo> getGame(@PathVariable("id") Game game) {
    throw new UnsupportedOperationException();
  }

  @Patch("/{id}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<GameInfo> updateGame(
      Principal principal,
      @PathVariable("id") Game game,
      @Valid @Body GameSettings settings
  ) {
    throw new UnsupportedOperationException();
  }

  @Delete("/{id}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> deleteGame(Principal principal, @PathVariable("id") Game game) {
    throw new UnsupportedOperationException();
  }

  @Post("/{id}/start")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> startGame(Principal principal, @PathVariable("id") Game game) {
    throw new UnsupportedOperationException();
  }

  @Get("/{id}/template")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Produces(MediaType.TEXT_PLAIN)
  public HttpResponse<String> getCodeTemplate(
      Principal principal,
      @PathVariable("id") Game game,
      @QueryValue("lang") String language
  ) {
    throw new UnsupportedOperationException();
  }

  @ExecuteOn(TaskExecutors.IO)
  @Get("/{id}/events")
  @Produces(MediaType.TEXT_EVENT_STREAM)
  public Publisher<Event<GameEvent>> subscribeToGameEvents(
      @Nullable Principal principal,
      @PathVariable("id") Game game
  ) {
    throw new UnsupportedOperationException();
  }
}
