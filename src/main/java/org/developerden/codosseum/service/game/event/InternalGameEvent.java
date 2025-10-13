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

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.event.GameEvent;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.player.GamePlayer;
import org.developerden.codosseum.service.game.GameCommand;

/// An event that happened to a [Game]
/// This is the internal version of [GameEvent], and is used for internal messaging.
///
/// Some events may not have a public counterpart, see [EventMapper]
///
/// See also [GameCommand]
public sealed interface InternalGameEvent {
  UUID gameId();

  ///  Emitted when a new game is created and the lobby is opened
  record GameCreated(UUID gameId, GamePlayer creator) implements InternalGameEvent {
  }

  record PlayerJoined(UUID gameId, GamePlayer player) implements InternalGameEvent {
  }

  /**
   * Emitted when the warmup countdown begins.
   */
  record WarmupStarted(UUID gameId, Duration warmupLength) implements InternalGameEvent {
  }

  /**
   * Emitted when the game transitions to in-progress.
   */
  record GameStarted(UUID gameId) implements InternalGameEvent {
  }

  record ChallengeSet(UUID gameId, ChallengeInfo challengeInfo) implements InternalGameEvent {

  }

  record RoundStarted(UUID gameId, @Nonnull ChallengeInfo challenge, int roundNumber,
                      @Nonnull Duration roundLength)
      implements InternalGameEvent {
  }

  record RoundEnded(UUID gameId, int roundNumber) implements InternalGameEvent {
  }
}
