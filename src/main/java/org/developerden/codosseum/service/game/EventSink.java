package org.developerden.codosseum.service.game;

public interface EventSink {
    void publish(GameEvent event);
}