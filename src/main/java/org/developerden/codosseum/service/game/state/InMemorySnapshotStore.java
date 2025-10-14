
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.developerden.codosseum.model.GameState;

@Singleton
public final class InMemorySnapshotStore implements SnapshotStore {
  private final ConcurrentMap<UUID, GameState> store = new ConcurrentHashMap<>();
  private final InitialGameStateProvider initialProvider;

  public InMemorySnapshotStore(InitialGameStateProvider initialProvider) {
    this.initialProvider = initialProvider;
  }

  @Override
  public Optional<GameState> load(UUID gameId) {
    return Optional.ofNullable(store.get(gameId));
  }

  @Override
  public void save(UUID gameId, GameState state) {
    store.put(gameId, state);
  }

  @Override
  public GameState createInitial(UUID gameId) {
    var state = initialProvider.create(gameId);
    store.put(gameId, state);
    return state;
  }
}
