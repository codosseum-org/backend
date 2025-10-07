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
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.developerden.codosseum.auth.GameAuthorized;
import org.developerden.codosseum.auth.GameRole;
import org.developerden.codosseum.controller.binder.GameParam;
import org.developerden.codosseum.dto.GameJoinResponse;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.service.GameService;

import java.security.Principal;
import java.util.UUID;

@Validated
@Controller("/games/{id}/players")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class PlayerController {

    private final GameService gameService;

    public PlayerController(GameService gameService) {
        this.gameService = gameService;
    }

    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<Players> getPlayers(@PathVariable("id") UUID id, @Parameter(hidden = true) @GameParam Game game) {
        // TODO this route seems very redundant and unhelpful
        return gameService.getGame(game.id())
                .map(g -> HttpResponse.ok(g.players()))
                .orElse(HttpResponse.notFound());
    }

    @Post
    public HttpResponse<GameJoinResponse> joinGame(
            @PathVariable("id") UUID id, @Parameter(hidden = true) @GameParam Game game,
            @Valid @Body Player player
    ) {
        throw new UnsupportedOperationException();
    }


    @Delete("/@self")
    @GameAuthorized(GameRole.PLAYER)
    public HttpResponse<Void> leaveGame(Principal principal, @PathVariable("id") Game game) {
        throw new UnsupportedOperationException();
    }

}
