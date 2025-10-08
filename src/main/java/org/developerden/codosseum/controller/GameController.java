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
import io.micronaut.http.HttpStatus;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.auth.GameAuthorized;
import org.developerden.codosseum.auth.GameRole;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.service.GameService;
import org.developerden.codosseum.service.game.event.SseEventSink;
import org.reactivestreams.Publisher;

@Validated
@Controller("/games")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GameController {

  private final GameService gameService;

  private final SseEventSink eventSink;

  public GameController(GameService gameService, SseEventSink eventSink) {
    this.gameService = gameService;
    this.eventSink = eventSink;
  }

  @Post
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ApiResponse(
      responseCode = "201",
      description = "new game created",
      content = @Content(),
      headers = @Header(
          name = "Location",
          description = "URL of the newly created game",
          required = true,
          schema = @Schema(
              type = "string",
              format = "uri-reference"
          )
      )
  )
  public HttpResponse<GameCreateResponse> createGame(@Valid @Body GameCreateRequest request) {
    GameCreateResponse response = gameService.createGame(request);
    return HttpResponse.created(response, URI.create(response.id().toString()));
  }

  @Get("/{id}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<GameInfo> getGame(@PathVariable("id") UUID gameId) {
    Optional<GameInfo> game = gameService.getGame(gameId);
    if (game.isEmpty()) {
      return HttpResponse.notFound();
    }
    return HttpResponse.ok(game.get());
  }

  @Patch("/{id}")
  @GameAuthorized(GameRole.ADMIN)
  public HttpResponse<GameInfo> updateGame(
      Principal principal,
      @PathVariable("id") String gameId,
      @Valid @Body GameSettings settings
  ) {
    return HttpResponse.ok(gameService.updateGame(gameId, settings));
  }

  @Delete("/{id}")
  @GameAuthorized(GameRole.ADMIN)
  public HttpResponse<Void> deleteGame(Principal principal, @PathVariable("id") String gameId) {
    gameService.deleteGame(gameId);
    return HttpResponse.noContent();
  }

  @Post("/{id}/start")
  @GameAuthorized(GameRole.ADMIN)
  @Operation(operationId = "startGame",
      summary = "Start a game",
      description = "Forcefully start a game, regardless of player-count and warmup time")
  @ApiResponse(
      responseCode = "204",
      description = "Successfully started the game. "
          + "Further info will be received via server-sent events."
  )
  @ApiResponse(
      responseCode = "409",
      description = "Game is already running or finished"
  )

  public HttpResponse<Void> startGame(Principal principal, @PathVariable("id") UUID gameId) {
    try {
      gameService.startGame(gameId);
    } catch (IllegalStateException e) {
      return HttpResponse.status(HttpStatus.CONFLICT);
    }

    return HttpResponse.noContent();
  }

  @Get("/{id}/template")
  @GameAuthorized(GameRole.PLAYER)
  @Produces(MediaType.TEXT_PLAIN)
  public HttpResponse<String> getCodeTemplate(
      Principal principal,
      @PathVariable("id") String gameId,
      // add custom validation annotation here
      @QueryValue("lang") String language
  ) {
    return HttpResponse.ok(gameService.getTemplate(gameId, language));
  }

  @Post("/{id}/restart")
  @GameAuthorized(GameRole.PLAYER)
  public HttpResponse<GameCreateResponse> restartGame(
      Principal principal,
      @PathVariable("id") String gameId,
      @Valid @Body GameSettings settings
  ) {
    GameCreateResponse response = gameService.restartGame(gameId);
    return HttpResponse.created(response, URI.create(response.id().toString()));
  }

  @ExecuteOn(TaskExecutors.IO)
  @Get("/{id}/events")
  @Produces(MediaType.TEXT_EVENT_STREAM)
  @Secured(SecurityRule.IS_ANONYMOUS)
  public Publisher<Event<GameEvent>> subscribeToGameEvents(
      @Nullable Principal principal,
      @PathVariable("id") UUID gameId
  ) {
    return eventSink.subscribeToPublicSse(gameId);
  }

}
