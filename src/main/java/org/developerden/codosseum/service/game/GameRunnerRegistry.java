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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GameRunnerRegistry {
  private final GameRunnerFactory factory;
  private final Map<UUID, GameRunner> runners = new ConcurrentHashMap<>();

  public GameRunnerRegistry(GameRunnerFactory factory) {
    this.factory = factory;
  }

  public GameRunner getOrCreate(UUID gameId) {
    return runners.computeIfAbsent(gameId, factory::create);
  }


  public Optional<GameRunner> find(UUID gameId) {
    return Optional.ofNullable(runners.get(gameId));
  }

  public void stop(UUID gameId) {
    var r = runners.remove(gameId);
    if (r != null) {
      r.shutdown();
    }
  }
}
