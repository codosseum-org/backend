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

package org.developerden.codosseum.model.phase;

import io.swagger.v3.oas.annotations.media.Schema;

public enum GamePhaseKind {
  @Schema(description = """
      The game phase is not defined, usually indicating an error state.
       Clients should generally not have to handle this state.""")
  UNDEFINED,
  @Schema(description = "The game has not yet started and is waiting for more players before it can begin")
  WAITING_FOR_PLAYERS,
  @Schema(description = "The game is in a warmup phase and is ready to begin")
  WARMUP,
  @Schema(description = "The game is currently in progress")
  IN_PROGRESS,
  @Schema(description = "The current round of the game is over")
  ROUND_OVER,
  @Schema(description = "The game has ended")
  GAME_OVER
}
