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
import java.util.List;
import org.developerden.codosseum.challenges.client.api.DefaultApi;
import org.developerden.codosseum.challenges.client.model.Info;
import org.developerden.codosseum.repository.GameRepository;
import org.developerden.codosseum.service.game.GameCommand;
import org.developerden.codosseum.service.game.GameRunnerRegistry;
import org.developerden.codosseum.service.game.event.InternalGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GameStartedHandler implements ApplicationEventListener<InternalGameEvent> {

  private final DefaultApi defaultApi;
  private final GameRepository gameRepository;
  private final GameRunnerRegistry gameRunnerRegistry;
  private final Logger log = LoggerFactory.getLogger(GameStartedHandler.class);

  public GameStartedHandler(DefaultApi defaultApi, GameRepository gameRepository,
                            GameRunnerRegistry gameRunnerRegistry) {
    this.defaultApi = defaultApi;
    this.gameRepository = gameRepository;
    this.gameRunnerRegistry = gameRunnerRegistry;
  }

  @Override
  @Async
  public void onApplicationEvent(InternalGameEvent event) {
    var gameStarted = (InternalGameEvent.GameStarted) event;

    var game = gameRepository.findGameById(gameStarted.gameId())
        .orElseThrow();


    Info info = defaultApi.challengesRandomGet(
        List.of(),
        List.of()
    ).block();

    gameRunnerRegistry.find(game.id())
        .ifPresentOrElse(
            runner -> // use ifPresent in case of race condition where the game has ended
                runner.tell(new GameCommand.SetChallengeInfo(game.id(), info)),
            () -> log.warn("Game runner does not exist for game {}", game.id()));

  }

  @Override
  public boolean supports(InternalGameEvent event) {
    return event instanceof InternalGameEvent.GameStarted;
  }
}
