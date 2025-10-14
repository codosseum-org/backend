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

package org.developerden.codosseum.controller.binder;

import io.micronaut.core.bind.ArgumentBinder;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.web.router.UriRouteMatch;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.repository.GameRepository;

@Singleton
public class GameParamBinder implements TypedRequestArgumentBinder<Game> {
  private final GameRepository gameRepository;

  public GameParamBinder(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  @Override
  public Argument<Game> argumentType() {
    return Argument.of(Game.class);
  }

  @Override
  public BindingResult<Game> bind(ArgumentConversionContext<Game> context, HttpRequest<?> source) {
    // Check if the parameter has @GameParam annotation
    if (!context.getArgument().isAnnotationPresent(GameParam.class)) {
      return ArgumentBinder.BindingResult.UNSATISFIED;
    }

    // Get the path variable name from the annotation
    String pathVariableName = context.getArgument()
        .getAnnotation(GameParam.class)
        .stringValue()
        .orElse("id");

    // Extract the UUID from path variables using getParameters()
    String uuidStr = (String) source.getAttribute("micronaut.http.route.match", UriRouteMatch.class)
        .map(m -> m.getVariableValues().get(pathVariableName))
        .filter(x -> x instanceof String).orElse(null);


    if (uuidStr == null) {
      return ArgumentBinder.BindingResult.UNSATISFIED;
    }

    try {
      UUID gameId = UUID.fromString(uuidStr);
      Optional<Game> game = gameRepository.findGameById(gameId);

      if (game.isEmpty()) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Game not found");
      }

      return () -> game;
    } catch (IllegalArgumentException e) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid game ID format");
    }
  }
}
