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

package org.developerden.codosseum.service.game.state;

import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.UUID;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GamePlayers;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;

/**
 * Default initial state: lobby waiting for players.
 */
@Singleton
public class DefaultInitialGameStateProvider implements InitialGameStateProvider {
  @Override
  public GameState create(UUID gameId) {
    return GameStateBuilder.builder()
        .gameId(gameId)
        .phase(GamePhase.WAITING_FOR_PLAYERS)
        .players(new GamePlayers(null, new HashSet<>()))
        .currentRound(-1)
        .build();
  }
}
