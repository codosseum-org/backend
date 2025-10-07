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
import io.micronaut.http.annotation.*;
import io.micronaut.http.sse.Event;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.developerden.codosseum.auth.GameAuthorized;
import org.developerden.codosseum.auth.GameRole;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.service.GameService;
import org.reactivestreams.Publisher;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@Validated
@Controller("/games")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Post
    @Secured(SecurityRule.IS_ANONYMOUS)
    @ApiResponse(
            responseCode = "201",
            description = "new game created",
            content = @Content(),
            headers = {@Header(
                    name = "Location",
                    description = "URL of the newly created game",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            format = "uri-reference"
                    )
            )}
    )
    public HttpResponse<GameCreateResponse> createGame(@Valid @Body GameCreateRequest request) {
        GameCreateResponse response = gameService.createGame(request);
        return HttpResponse.created(response, URI.create(response.id().toString()));
    }

    @Get("/{id}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<GameInfo> getGame(@PathVariable("id") @Valid UUID gameId) {
        return HttpResponse.ok(gameService.getGame(gameId));
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
    public HttpResponse<Void> startGame(Principal principal, @PathVariable("id") String gameId) {
        gameService.startGame(gameId);
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
            @PathVariable("id") String gameId
    ) {
        throw new UnsupportedOperationException();
    }

}
