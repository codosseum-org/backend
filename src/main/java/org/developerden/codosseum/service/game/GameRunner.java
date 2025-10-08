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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.service.game.event.EventSink;
import org.developerden.codosseum.service.game.state.SnapshotStore;

public class GameRunner {
  private final UUID gameId;

  private final ExecutorService loop;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final EventSink eventSink;
  private final SnapshotStore snapshotStore;
  private volatile GameAggregate gameAggregate;

  public GameRunner(UUID gameId, EventSink eventSink, SnapshotStore snapshotStore,
                    GameAggregateFactory aggregateFactory) {
    this.gameId = gameId;
    this.eventSink = eventSink;
    this.snapshotStore = snapshotStore;

    this.loop = Executors.newSingleThreadExecutor(r -> new Thread(r, "game-" + gameId));

    GameState snapshot = this.snapshotStore.load(gameId)
        .orElseGet(() -> this.snapshotStore.createInitial(gameId));
    this.gameAggregate = aggregateFactory.create(gameId, snapshot);
  }

  public void tell(GameCommand cmd) {
    loop.execute(() -> handle(cmd));
  }

  public GameState getCurrentState() {
    return gameAggregate.getGameState();
  }

  private void handle(GameCommand cmd) {
    var result = gameAggregate.handle(cmd);
    this.gameAggregate = result.next();
    for (var event : result.events()) {
      eventSink.publish(event);
    }
    snapshotStore.save(gameId, gameAggregate.getGameState());
  }

  public void shutdown() {
    scheduler.shutdownNow();
    loop.shutdownNow();
  }


}
