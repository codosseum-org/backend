package org.developerden.codosseum.service.game.event;

public interface EventSink {
    void publish(InternalGameEvent event);
}