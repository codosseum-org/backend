package org.developerden.codosseum.model.player;

public sealed interface GamePlayer permits EphemeralPlayer {
    String name();
}
