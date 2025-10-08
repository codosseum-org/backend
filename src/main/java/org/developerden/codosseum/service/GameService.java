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

package org.developerden.codosseum.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.Players;
import org.developerden.codosseum.mode.GameModeFactory;
import org.developerden.codosseum.mode.GameModeType;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.repository.GameRepository;
import org.developerden.codosseum.service.game.GameCommand;
import org.developerden.codosseum.service.game.GameRunner;
import org.developerden.codosseum.service.game.GameRunnerRegistry;
import org.developerden.codosseum.service.game.event.EventSink;
import org.developerden.codosseum.service.game.state.SnapshotStore;
import org.developerden.codosseum.utils.CollectionUtils;

@Singleton
public class GameService {
  private final GameRepository gameRepository;
  private final GameModeFactory gameModeFactory;
  private final GameRunnerRegistry gameRunnerRegistry;
  private final SnapshotStore snapshotStore;

  private final EventSink eventSink;

  public @Inject GameService(GameRepository gameRepository, GameModeFactory gameModeFactory,
                             GameRunnerRegistry gameRunnerRegistry, SnapshotStore snapshotStore,
                             EventSink eventSink) {
    this.gameRepository = gameRepository;
    this.gameModeFactory = gameModeFactory;
    this.gameRunnerRegistry = gameRunnerRegistry;
    this.snapshotStore = snapshotStore;
    this.eventSink = eventSink;
  }

  private String generateAdminKey() {
    return UUID.randomUUID().toString();
  }

  public GameCreateResponse createGame(GameCreateRequest request) {
    var gameModeTypes = EnumSet.allOf(GameModeType.class);
    if (Optional.ofNullable(request.settings().allowedGameModes()).map(modes -> !modes.isEmpty())
        .orElse(false)) {
      gameModeTypes.retainAll(request.settings().allowedGameModes());
    }
    var gameModeType = CollectionUtils.pickRandom(gameModeTypes);
    var gameMode = gameModeFactory.fromType(gameModeType);

    var game = new Game(UUID.randomUUID(), generateAdminKey(), request.settings(), new Players(
        new HashSet<>(),
        request.player()
    ), gameMode);

    gameRepository.insertGame(game);

    gameRunnerRegistry.getOrCreate(game.id())
        .tell(new GameCommand.CreateGame(game.id()));

    return new GameCreateResponse(game.adminKey(), game.id());
  }

  public GameInfo updateGame(String gameId, GameSettings settings) {
    throw new UnsupportedOperationException();
  }

  public void deleteGame(String gameId) {
    throw new UnsupportedOperationException();
  }

  public Optional<GameInfo> getGame(UUID id) {
    var gameOpt = gameRepository.findGameById(id);

    if (gameOpt.isEmpty()) {
      return Optional.empty();
    }

    var stateOpt = gameRunnerRegistry
        .find(id)
        .map(GameRunner::getCurrentState)
        .or(() -> snapshotStore.load(id));

    var phase = stateOpt.map(GameState::phase)
        .orElseThrow(() -> new IllegalStateException("No game state found for game " + id));

    return gameOpt
        .map(game -> new GameInfo(
            game.settings(),
            game.id(),
            game.mode(),
            game.players(),
            phase,
            0,
            0,
            1,
            new ArrayList<>()
        ));
  }

  public void startGame(UUID gameId) {
    GameRunner runner = gameRunnerRegistry.getOrCreate(gameId);
    if (runner.getCurrentState().phase() != GamePhase.WAITING_FOR_PLAYERS) {
      throw new IllegalStateException("Game is already running or finished");
    }

    runner.tell(new GameCommand.StartGame(gameId));
  }


  public String getTemplate(String gameId, String lang) {
    throw new UnsupportedOperationException();
  }

  public void initiateNextRound(String gameId) {
    throw new UnsupportedOperationException();
  }

  public GameCreateResponse restartGame(String gameId) {
    throw new UnsupportedOperationException();
  }

  public Optional addPlayer(UUID id, @Valid Player player) {
    return null;
  }
}
