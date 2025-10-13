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

package org.developerden.codosseum.service.game.process;

import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.developerden.codosseum.service.game.GameCommand;
import org.developerden.codosseum.service.game.GameRunnerRegistry;
import org.developerden.codosseum.service.game.event.InternalGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for challenges being set for a game, and starts the round when they are.
 */
@Singleton
public class ChallengeSetHandler implements ApplicationEventListener<InternalGameEvent> {
  private final GameRunnerRegistry gameRunnerRegistry;

  private final Logger log = LoggerFactory.getLogger(ChallengeSetHandler.class);

  @Inject
  public ChallengeSetHandler(GameRunnerRegistry gameRunnerRegistry) {
    this.gameRunnerRegistry = gameRunnerRegistry;
  }

  @Override
  public void onApplicationEvent(InternalGameEvent event) {
    if (event instanceof InternalGameEvent.ChallengeSet challengeSet) {
      gameRunnerRegistry.find(challengeSet.gameId())
          .ifPresentOrElse(runner -> runner.tell(new GameCommand.StartRound(
              challengeSet.gameId()
          )), () -> log.warn("Tried to start round for non-existent game {}",
              challengeSet.gameId()));
    }
  }

  @Override
  public boolean supports(InternalGameEvent event) {
    return event instanceof InternalGameEvent.ChallengeSet;
  }
}
