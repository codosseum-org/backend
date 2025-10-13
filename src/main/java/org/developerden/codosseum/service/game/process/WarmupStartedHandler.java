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
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import org.developerden.codosseum.challenges.client.api.DefaultApi;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.model.phase.WarmupPhase;
import org.developerden.codosseum.repository.GameRepository;
import org.developerden.codosseum.service.game.GameCommand;
import org.developerden.codosseum.service.game.GameRunnerRegistry;
import org.developerden.codosseum.service.game.event.InternalGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for games going into their {@link WarmupPhase} and queries a random challenge for them.
 */
@Singleton
public class WarmupStartedHandler implements ApplicationEventListener<InternalGameEvent> {

  private final DefaultApi defaultApi;
  private final GameRepository gameRepository;
  private final GameRunnerRegistry gameRunnerRegistry;
  private final Logger log = LoggerFactory.getLogger(WarmupStartedHandler.class);

  public WarmupStartedHandler(DefaultApi defaultApi, GameRepository gameRepository,
                              GameRunnerRegistry gameRunnerRegistry) {
    this.defaultApi = defaultApi;
    this.gameRepository = gameRepository;
    this.gameRunnerRegistry = gameRunnerRegistry;
  }

  @Override
  @Async
  public void onApplicationEvent(InternalGameEvent event) {
    var gameStarted = (InternalGameEvent.WarmupStarted) event;

    var game = gameRepository.findGameById(gameStarted.gameId())
        .orElseThrow();


    ChallengeInfo info = defaultApi.challengesRandomGet(
        null,
        null
    ).block();

    gameRunnerRegistry.sendCommand(new GameCommand.SetChallengeInfo(game.id(), info));
  }

  @Override
  public boolean supports(InternalGameEvent event) {
    return event instanceof InternalGameEvent.WarmupStarted;
  }
}
