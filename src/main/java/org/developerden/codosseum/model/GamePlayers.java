package org.developerden.codosseum.model;

import org.developerden.codosseum.dto.Player;

import java.util.Set;

public record GamePlayers(Player admin, Set<Player> others) {
}
