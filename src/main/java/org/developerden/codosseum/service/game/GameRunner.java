package org.developerden.codosseum.service.game;

import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.service.game.state.SnapshotStore;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameRunner {
    private final UUID gameId;

    private final ExecutorService loop;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final EventSink eventSink;
    private final SnapshotStore snapshotStore;
    private volatile GameAggregate gameAggregate;

    public GameRunner(UUID gameId, EventSink eventSink, SnapshotStore snapshotStore, GameAggregateFactory aggregateFactory) {
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
