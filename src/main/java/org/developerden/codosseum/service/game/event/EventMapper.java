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

import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.stream.Collectors;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.event.PlayerJoinEvent;
import org.developerden.codosseum.model.GamePlayers;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.GamePlayer;

@Singleton
public class EventMapper {

  public Optional<GameEvent> fromInternal(InternalGameEvent internalEvent) {
    return switch (internalEvent) {
      case InternalGameEvent.GameCreated ignored -> Optional.empty();

      case InternalGameEvent.PlayerJoined playerJoined -> Optional.of(
          new PlayerJoinEvent(playerJoined.gameId(), fromGamePlayer(playerJoined.player()))
      );
    };
  }

  private Optional<Players> fromGamePlayers(@Nonnull GamePlayers gamePlayers) {
    var players = new Players(
        gamePlayers.others().stream()
            .map(this::fromGamePlayer)
            .collect(Collectors.toSet()),
        fromGamePlayer(gamePlayers.admin())
    );
    return Optional.of(players);
  }

  private Player fromGamePlayer(@Nonnull GamePlayer player) {
    return switch (player) {
      case EphemeralPlayer(var name, var ignored, var ignored2) -> (new Player(name));
    };
  }
}
