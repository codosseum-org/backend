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
import org.developerden.codosseum.challenges.client.model.Info;
import org.developerden.codosseum.model.player.GamePlayer;

/**
 * A command that can be executed on a {@link org.developerden.codosseum.model.Game}.
 */
public sealed interface GameCommand {
  UUID gameId();


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

  record AddPlayer(UUID gameId, GamePlayer player) implements GameCommand {
  }

  record SetChallengeInfo(UUID gameId, Info info) implements GameCommand {
  }


}
