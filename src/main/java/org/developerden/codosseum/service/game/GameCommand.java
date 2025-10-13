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

import java.util.UUID;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.player.GamePlayer;

/**
 * A command that can be executed on a {@link org.developerden.codosseum.model.Game} by sending it to its {@link GameRunner}.
 */
public sealed interface GameCommand {
  /**
   * The id of the game this command is for.
   *
   * @return the game id
   */
  UUID gameId();


  /**
   * Command to create a new game, triggering initial state setup.
   *
   * @param gameId  the id of the game to create. This should be unique.
   * @param creator the player who created the game.
   */
  record CreateGame(UUID gameId, GamePlayer creator) implements GameCommand {
  }

  /**
   * Command to start the warmup phase of a game.
   *
   * @param gameId the id of the game to start the warmup for
   */
  record StartWarmup(UUID gameId) implements GameCommand {
  }

  /**
   * Command to start a game.
   *
   * @param gameId the id of the game to start
   */
  record StartGame(UUID gameId) implements GameCommand {
  }

  /**
   * Command to add a player to a game.
   *
   * @param gameId the id of the game to add the player to
   * @param player the player to add
   */
  record AddPlayer(UUID gameId, GamePlayer player) implements GameCommand {
  }

  /**
   * Command to set the current challenge for a game.
   * It is undefined what this does if the game is not in a state to accept a new challenge (i.e. is already in progress).
   * Generally, this command is only safe to send if the game is in the {@link GamePhase#WAITING_FOR_PLAYERS}, {@link GamePhase#WARMUP}, or {@link GamePhase#ROUND_OVER} phases.
   *
   * @param gameId the id of the game to set the challenge for
   * @param info   the challenge info to set
   */
  record SetChallengeInfo(UUID gameId, ChallengeInfo info) implements GameCommand {
  }

  /**
   * Command to start a new round in a game.
   * The game phase should be {@link GamePhase#IN_PROGRESS} for this to have any effect,
   * and an error may be thrown if it is not.
   *
   * @param gameId the id of the game to start the round for
   */
  record StartRound(UUID gameId) implements GameCommand {
  }

  /**
   * Command to end the current round in a game.
   * The game phase should be {@link GamePhase#IN_PROGRESS} for this to have any effect.
   *
   * @param gameId the id of the game to end the round for
   */
  record EndRound(UUID gameId) implements GameCommand {
  }

}
