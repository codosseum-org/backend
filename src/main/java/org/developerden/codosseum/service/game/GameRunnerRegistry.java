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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

/**
 * Registry for active game runners.
 * Uses a concurrent hash map to store the runners, allowing for thread-safe access and modification.
 *
 * <p>This also provides a convenient way to send commands to the appropriate game runner without needing to manually look it up each time - see {@link #sendCommand(GameCommand)} and {@link #trySendCommand(GameCommand)}.
 *
 * @see GameRunner
 */
@Singleton
public class GameRunnerRegistry {
  private final GameRunnerFactory factory;
  private final Map<UUID, GameRunner> runners = new ConcurrentHashMap<>();

  @Inject
  GameRunnerRegistry(GameRunnerFactory factory) {
    this.factory = factory;
  }

  /**
   * Get an existing game runner or create a new one if it doesn't exist.
   * This method should be used sparingly, considering whether a runner is expected to exist already or not.
   *
   * <p>For example, when creating a game, it's expected that no runner exists yet, so creating one is appropriate.
   * Conversely, when sending a command to an existing game, it's expected that the runner already exists, and its absence should be treated as an error.
   * As a law of thumb, assume that a runner should exist unless you are in the process of creating a new game.
   *
   * <p>It is generally better to use {@link #find(UUID)} or {@link #sendCommand(GameCommand)} depending on the context.
   *
   * @param gameId the id of the game
   * @return the game runner
   */
  public GameRunner getOrCreate(UUID gameId) {
    return runners.computeIfAbsent(gameId, factory::create);
  }


  /**
   * Find an existing game runner by game id.
   * Returns an empty optional if no runner exists for the given game id.
   *
   * @param gameId the id of the game
   * @return an optional containing the game runner if it exists, or empty if it doesn't
   */
  public Optional<GameRunner> find(UUID gameId) {
    return Optional.ofNullable(runners.get(gameId));
  }

  /**
   * Send a command to the game runner for the given game id, looking up the {@link GameRunner} from the registry automatically.
   * If no runner exists for the given game id, an {@link IllegalStateException} is thrown.
   *
   * <p>This is the preferred method to use when sending commands to existing games, as it enforces the expectation that the game runner should already exist.
   *
   * @param command the command to send.
   * @see #getOrCreate(UUID) for information about when a runner is expected to exist or not.
   */
  public void sendCommand(@Nonnull GameCommand command) {
    var runner = runners.get(command.gameId());
    if (runner != null) {
      runner.tell(command);
    } else {
      throw new IllegalStateException("No game runner found for game id: " + command.gameId());
    }
  }

  /**
   * Try to send a command to the game runner for the given game id.
   * If no runner exists for the given game id, the command is silently ignored.
   *
   * @param command the command to send.
   */
  public void trySendCommand(@Nonnull GameCommand command) {
    var runner = runners.get(command.gameId());
    if (runner != null) {
      runner.tell(command);
    }
  }

  /**
   * Stop and remove the game runner for the given game id.
   *
   * @param gameId the id of the game
   */
  public void stop(UUID gameId) {
    var r = runners.remove(gameId);
    if (r != null) {
      r.shutdown();
    }
  }
}
