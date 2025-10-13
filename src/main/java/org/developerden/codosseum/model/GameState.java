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

package org.developerden.codosseum.model;

import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.model.phase.GamePhase;
import org.developerden.codosseum.model.phase.WithPlayersPhase;

/**
 * Internal model representing the current mutable state of a game.
 *
 * @param gameId               the unique identifier of the game
 * @param phase                the current phase of the game
 * @param currentChallengeInfo the current challenge information, if a challenge is active
 * @param currentRound         the current round number of the game
 */
@RecordBuilder
@RecordBuilder.Options(defaultNotNull = true)
public record GameState(@Nonnull UUID gameId,
                        @Nonnull GamePhase phase,
                        @Nullable ChallengeInfo currentChallengeInfo,
                        boolean acceptingSolutions,
                        @Nullable @Positive Integer currentRound
) implements GameStateBuilder.With {

  public GameState updatePlayers(UnaryOperator<GamePlayers> mutate) {
    var phase = phase();
    if (phase instanceof WithPlayersPhase wp) {
      var newPlayers = mutate.apply(wp.players());
      var newPhase = (GamePhase) wp.withPlayers(newPlayers);
      return GameStateBuilder.from(this).withPhase(newPhase);
    }
    throw new IllegalStateException(
        "Phase does not contain players: " + phase.getClass().getSimpleName());
  }

  public GamePlayers players() {
    var phase = phase();
    if (phase instanceof WithPlayersPhase wp) {
      return wp.players();
    }
    throw new IllegalStateException(
        "Phase does not contain players: " + phase.getClass().getSimpleName());
  }
}
