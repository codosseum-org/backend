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
import javax.annotation.Nonnull;
import org.developerden.codosseum.dto.phase.ApiGamePhase;
import org.developerden.codosseum.model.GameState;

/**
 * Internal model of a phase of the game.
 * The phase is the state of a game that is specific to the current part of the game lifecycle.
 * For example, the state needed while waiting for players to join is different to the state needed while the game is in progress.
 *
 * @see GamePhaseKind for the different kinds of phases.
 * @see GameState for state across the entire game lifecycle.
 * @see ApiGamePhase for the DTO representation of this interface.
 */
@Schema(description = "A phase of the game.")
public sealed interface GamePhase
    permits InProgressPhase, UndefinedPhase, WaitingForPlayersPhase, WarmupPhase, WithPlayersPhase {
  /**
   * The kind of phase this is. Serves as a simple discriminator for serialization or for checking the type of phase.
   *
   * @return the kind of phase, which should generally be unique across implementations of this interface.
   */
  @Nonnull GamePhaseKind getKind();
}
