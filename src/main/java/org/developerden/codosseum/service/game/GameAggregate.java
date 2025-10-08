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

package org.developerden.codosseum.service.game;

import java.util.List;
import java.util.UUID;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GamePlayersBuilder;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;
import org.developerden.codosseum.service.game.event.InternalGameEvent;

public class GameAggregate {
  private final UUID gameId;

  private final GameState gameState;

  public GameAggregate(UUID gameId, GameState gameState) {
    this.gameId = gameId;
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return gameState;
  }

  public Result handle(GameCommand cmd) {
    var events = decide(cmd);
    var newState = applyAll(gameState, events);

    var next = new GameAggregate(gameId, newState);

    return new Result(events, next);
  }

  private List<InternalGameEvent> decide(GameCommand cmd) {
    if (!cmd.gameId().equals(gameId)) {
      throw new IllegalArgumentException("Command gameId does not match aggregate gameId");
    }

    return switch (cmd) {
      case GameCommand.CreateGame(var id) -> {
        if (gameState.phase() != GamePhase.WAITING_FOR_PLAYERS) {
          throw new IllegalStateException(
              "Cannot create game that is not in WAITING_FOR_PLAYERS phase");
        } else {
          yield List.of(new InternalGameEvent.GameCreated(gameId));
        }
      }
      case GameCommand.StartGame(var id) -> {
        if (gameState.phase() != GamePhase.WAITING_FOR_PLAYERS) {
          throw new IllegalStateException(
              "Cannot start game that is not in WAITING_FOR_PLAYERS phase");
        } else {
          // TODO: check if enough players and actually do something
          yield List.of();
        }
      }
      case GameCommand.AddPlayer(var id, var player) -> {
        if (gameState.phase() != GamePhase.WAITING_FOR_PLAYERS) {
          throw new IllegalStateException(
              "Cannot join game that is not in WAITING_FOR_PLAYERS phase");
        } else {
          yield List.of(new InternalGameEvent.PlayerJoined(gameId, player));
        }
      }

    };
  }

  private GameState applyAll(GameState state, List<InternalGameEvent> events) {
    var newState = state;
    for (var event : events) {
      newState = apply(newState, event);
    }
    return newState;
  }

  private GameState apply(GameState state, InternalGameEvent event) {
    if (!event.gameId().equals(gameId)) {
      throw new IllegalArgumentException("Event gameId does not match aggregate gameId");
    }
    return switch (event) {
      case InternalGameEvent.PlayerJoined(var gameId, var player) -> GameStateBuilder.from(state)
          .withPlayers(
              GamePlayersBuilder.builder(state.players())
                  .addOthers(player)
                  .build()
          );
      case InternalGameEvent.GameCreated(var gameId) -> state; // no-op for now
    };

  }


  public record Result(List<InternalGameEvent> events, GameAggregate next) {
  }

}
