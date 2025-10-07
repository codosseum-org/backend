package org.developerden.codosseum.service.game.event;

import io.micronaut.http.sse.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.developerden.codosseum.event.GameEvent;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SseEventSink implements EventSink {
    private final Map<UUID, Sinks.Many<InternalGameEvent>> sinks = new ConcurrentHashMap<>();
    private final EventMapper eventMapper;
    private final Logger logger = LoggerFactory.getLogger(SseEventSink.class);

    @Inject
    public SseEventSink(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public void publish(InternalGameEvent event) {
        UUID gameId = event.gameId();
        logger.atInfo().log("Publishing event {} for game {}", event, gameId);
        var sink = sinks.computeIfAbsent(gameId, __ -> Sinks.many().multicast().onBackpressureBuffer());
        sink.tryEmitNext(event);
    }

    public Publisher<Event<InternalGameEvent>> subscribeToSse(UUID gameId) {
        var sink = sinks.computeIfAbsent(gameId, __ -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux().map(event -> Event.of(event).name(event.getClass().getSimpleName()));
    }

    public Publisher<Event<GameEvent>> subscribeToPublicSSE(UUID gameId) {
        var sink = sinks.computeIfAbsent(gameId, __ -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux()
                .flatMap(e -> eventMapper.fromInternal(e)
                        .map(publicEvent -> Event.of(publicEvent).name(publicEvent.getClass().getSimpleName()))
                        .map(Mono::just)
                        .orElse(Mono.empty())
                );
    }

    public void close(UUID gameId) {
        var sink = sinks.remove(gameId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }
}
