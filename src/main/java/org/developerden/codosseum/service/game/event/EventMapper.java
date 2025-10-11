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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.developerden.codosseum.dto.PlayersMapper;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.event.PlayerJoinEvent;
import org.developerden.codosseum.event.RoundStartEvent;
import org.developerden.codosseum.event.SyncEvent;
import org.developerden.codosseum.model.GamePhase;

@Singleton
public class EventMapper {
  private final PlayersMapper playersMapper;

  @Inject
  public EventMapper(PlayersMapper playersMapper) {
    this.playersMapper = playersMapper;
  }

  public Optional<GameEvent> fromInternal(InternalGameEvent internalEvent) {
    return switch (internalEvent) {
      case InternalGameEvent.GameCreated ignored -> Optional.empty();

      case InternalGameEvent.PlayerJoined playerJoined -> Optional.of(
          new PlayerJoinEvent(playerJoined.gameId(), playersMapper.toDto(playerJoined.player()))
      );

      case InternalGameEvent.WarmupStarted(var ignored, var warmupLength) -> Optional.of(
          new SyncEvent(GamePhase.WARMUP,
              // how long until warmup ends
              Math.toIntExact(warmupLength.toSeconds())
          )
      );

      case InternalGameEvent.GameStarted ignored -> Optional.empty();
      case InternalGameEvent.ChallengeSet challengeSet -> Optional.of(
          new RoundStartEvent(
              challengeSet.challengeInfo(),
              1
          )
      );
    };
  }
}
