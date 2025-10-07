package org.developerden.codosseum.service.game;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.developerden.codosseum.service.game.state.SnapshotStore;

import java.util.UUID;

@Singleton
public class GameRunnerFactory {

    private final EventSink eventSink;
    private final SnapshotStore snapshotStore;
    private final GameAggregateFactory aggregateFactory;

    @Inject
    public GameRunnerFactory(EventSink eventSink, SnapshotStore snapshotStore, GameAggregateFactory aggregateFactory) {
        this.eventSink = eventSink;
        this.snapshotStore = snapshotStore;
        this.aggregateFactory = aggregateFactory;
    }

    public GameRunner create(UUID gameId) {
        return new GameRunner(gameId, eventSink, snapshotStore, aggregateFactory);
    }
}
