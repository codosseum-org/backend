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

import jakarta.inject.Singleton;
import java.util.UUID;
import org.developerden.codosseum.model.GameState;

/**
 * Implementation of {@link GameAggregateFactory}, used to create new instances of {@link GameAggregate}.
 */
@Singleton
public class GameAggregateFactoryImpl implements GameAggregateFactory {

  @Override
  public GameAggregate create(UUID gameId, GameState snapshot) {
    return new GameAggregate(gameId, snapshot);
  }
}
