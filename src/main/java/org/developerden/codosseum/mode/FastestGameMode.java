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

package org.developerden.codosseum.mode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.PlayerRoundResult;

public class FastestGameMode implements GameMode {
  @Override
  public GameModeType getType() {
    return GameModeType.FASTEST;
  }

  @Override
  public double computeScore(Player player, List<PlayerRoundResult> results) {
    var orderedScores = results.stream()
        .sorted(Comparator.comparing(PlayerRoundResult::timeLeft).reversed())
        .toList();

    // find the result of the player
    Optional<PlayerRoundResult> any = results.stream()
        .filter(res -> res.player().equals(player))
        .findAny();

    // warn?
    return any.map(playerRoundResult -> orderedScores.indexOf(playerRoundResult) + 1.0)
        .orElse(0.0);
  }
}
