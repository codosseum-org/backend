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

import io.micronaut.scheduling.TaskScheduler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.service.game.effect.SideEffect;
import org.developerden.codosseum.service.game.event.EventSink;
import org.developerden.codosseum.service.game.state.SnapshotStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overseer of a single game's state and progression.
 * Runs all game logic in a single-threaded executor.
 *
 */
public class GameRunner {
  private static final Logger log = LoggerFactory.getLogger(GameRunner.class);
  private final UUID gameId;

  private final ExecutorService loop;
  private final TaskScheduler scheduler;
  private final EventSink eventSink;
  private final SnapshotStore snapshotStore;
  private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();
  private volatile GameAggregate gameAggregate;

  public GameRunner(UUID gameId, TaskScheduler scheduler, EventSink eventSink,
                    SnapshotStore snapshotStore,
                    GameAggregateFactory aggregateFactory) {
    this.gameId = gameId;
    this.scheduler = scheduler;
    this.eventSink = eventSink;
    this.snapshotStore = snapshotStore;

    this.loop = Executors.newSingleThreadExecutor(r -> new Thread(r, "game-" + gameId));

    GameState snapshot = this.snapshotStore.load(gameId)
        .orElseGet(() -> this.snapshotStore.createInitial(gameId));
    this.gameAggregate = aggregateFactory.create(gameId, snapshot);
  }

  /**
   * Send a command to this game runner.
   *
   * @param cmd the command to handle.
   */
  public void tell(GameCommand cmd) {
    log.debug("tell game command: {}", cmd);
    loop.execute(() -> handle(cmd));
  }

  public GameState getCurrentState() {
    return gameAggregate.getGameState();
  }

  /**
   * Handle a command. Must be called from the game loop thread for safety.
   *
   * <p>The command is first sent to the {@link GameAggregate} for handling, which produces events and side effects.
   * Any events are published to the event sink, and side effects are executed.
   * Finally, the new game state is persisted in {@link SnapshotStore}.
   *
   * @param cmd the command to handle.
   */
  private void handle(GameCommand cmd) {
    log.debug("handle command: {}", cmd);
    var result = gameAggregate.handle(cmd);
    this.gameAggregate = result.next();
    for (var event : result.events()) {
      eventSink.publish(event);
    }
    // execute side effects after state mutation and publication
    for (var effect : result.effects()) {
      execute(effect);
    }
    snapshotStore.save(gameId, gameAggregate.getGameState());
  }

  private void execute(SideEffect effect) {
    log.debug("execute effect: {}", effect);
    switch (effect) {
      case SideEffect.ScheduleAfter(var key, var delay, var command) -> {
        // cancel any existing scheduled task for the same key
        var existing = scheduled.remove(key);
        if (existing != null) {
          log.info("Cancelling existing scheduled task with key {} in game {}", key, gameId);
          existing.cancel(false);
        }
        var future = scheduler.schedule(delay, () -> {
          scheduled.remove(key);
          tell(command);
        });
        scheduled.put(key, future);
        log.info("Scheduled task with key {} in game {} to run after {}", key, gameId, delay);
      }
      case SideEffect.CancelScheduled(var key) -> {
        var existing = scheduled.remove(key);
        if (existing != null) {
          existing.cancel(false);
          log.info("Cancelled scheduled task with key {} in game {}", key, gameId);
        }
      }
    }
  }

  /**
   * Shutdown this game runner, cancelling any scheduled tasks.
   * This should be called when the game is over and the runner is no longer needed.
   */
  public void shutdown() {
    // cancel scheduled tasks
    for (var entry : scheduled.entrySet()) {
      boolean cancel = entry.getValue().cancel(false);
      if (!cancel) {
        log.warn("Could not cancel scheduled task {} in game {}", entry.getKey(), gameId);
      }
    }
    scheduled.clear();
    loop.shutdownNow();
  }


}
