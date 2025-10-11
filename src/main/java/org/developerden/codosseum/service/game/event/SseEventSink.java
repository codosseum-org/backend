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

package org.developerden.codosseum.service.game.event;

import io.micronaut.http.sse.Event;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.UUID;
import org.developerden.codosseum.event.GameEvent;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Publishes {@link InternalGameEvent}s to subscribed SSE clients.
 */
@Singleton
public class SseEventSink {
  private final Sinks.Many<InternalGameEvent> eventSink =
      Sinks.many().multicast().onBackpressureBuffer();
  private final EventMapper eventMapper;
  private final Logger logger = LoggerFactory.getLogger(SseEventSink.class);


  @Inject
  public SseEventSink(EventMapper eventMapper) {
    this.eventMapper = eventMapper;
  }


  @EventListener
  public void on(InternalGameEvent event) {
    UUID gameId = event.gameId();
    logger.info("Publishing event {} for game {}", event, gameId);

    eventSink.tryEmitNext(event);
  }

  /**
   * Subscribe to all events across all games.
   * This is primarily for testing or logging purposes.
   */
  public Publisher<Event<GameEvent>> allEvents() {
    return eventSink.asFlux()
        .flatMap(this::fromInternal);
  }


  public Publisher<Event<GameEvent>> subscribeToPublicSse(UUID gameId) {
    return eventSink.asFlux()
        .filter(e -> e.gameId().equals(gameId))
        .flatMap(this::fromInternal);
  }

  private Mono<Event<GameEvent>> fromInternal(InternalGameEvent internalEvent) {
    return eventMapper.fromInternal(internalEvent)
        .map(publicEvent -> Event.of(publicEvent).name(publicEvent.getClass().getSimpleName()))
        .map(Mono::just)
        .orElse(Mono.empty());
  }
}
