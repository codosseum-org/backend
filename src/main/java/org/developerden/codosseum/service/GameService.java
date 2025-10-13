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
import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameJoinResponse;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.PlayersMapper;
import org.developerden.codosseum.dto.phase.PhaseMapper;
import org.developerden.codosseum.mode.GameModeFactory;
import org.developerden.codosseum.mode.GameModeType;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.phase.GamePhaseKind;
import org.developerden.codosseum.model.phase.WithPlayersPhase;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.repository.AuthRepository;
import org.developerden.codosseum.repository.GameRepository;
import org.developerden.codosseum.service.game.GameCommand;
import org.developerden.codosseum.service.game.GameRunner;
import org.developerden.codosseum.service.game.GameRunnerRegistry;
import org.developerden.codosseum.service.game.state.SnapshotStore;
import org.developerden.codosseum.utils.CollectionUtils;

@Singleton
public class GameService {
  private final GameRepository gameRepository;
  private final GameModeFactory gameModeFactory;
  private final GameRunnerRegistry gameRunnerRegistry;
  private final SnapshotStore snapshotStore;

  private final AuthRepository authRepository;
  private final PlayersMapper playersMapper;
  private final PhaseMapper phaseMapper;

  public @Inject GameService(GameRepository gameRepository, GameModeFactory gameModeFactory,
                             GameRunnerRegistry gameRunnerRegistry, SnapshotStore snapshotStore,
                             AuthRepository authRepository,
                             PlayersMapper playersMapper,
                             PhaseMapper phaseMapper) {
    this.gameRepository = gameRepository;
    this.gameModeFactory = gameModeFactory;
    this.gameRunnerRegistry = gameRunnerRegistry;
    this.snapshotStore = snapshotStore;
    this.authRepository = authRepository;
    this.playersMapper = playersMapper;
    this.phaseMapper = phaseMapper;
  }

  private String generateFreshKey() {
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

    var game = new Game(UUID.randomUUID(), request.settings(), gameMode);

    gameRepository.insertGame(game);

    var ephemeralPlayer =
        new EphemeralPlayer(request.player().name(), game.id(), generateFreshKey(), true);
    authRepository.save(game, ephemeralPlayer);
    gameRunnerRegistry.getOrCreate(game.id())
        .tell(new GameCommand.CreateGame(game.id(), ephemeralPlayer));

    return new GameCreateResponse(ephemeralPlayer.key(), game.id());
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
    var game = gameOpt.get();

    var state = gameRunnerRegistry
        .find(id)
        .map(GameRunner::getCurrentState)
        .or(() -> snapshotStore.load(id))
        .orElseThrow(() -> new IllegalStateException("No game state found for game " + id));

    var phase = state.phase();
    if (!(phase instanceof WithPlayersPhase playersPhase)) {
      throw new IllegalStateException("Game phase does not have players: " + phase);
    }

    return Optional.of(new GameInfo(
        game.settings(),
        game.id(),
        game.mode(),
        playersMapper.toDto(playersPhase.players()),
        phaseMapper.toDto(state.phase()),
        new ArrayList<>()
    ));
  }

  public void startGame(UUID gameId) {
    GameRunner runner = gameRunnerRegistry.getOrCreate(gameId);
    if (runner.getCurrentState().phase().getKind() != GamePhaseKind.WAITING_FOR_PLAYERS) {
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

  public Optional<GameJoinResponse> addPlayer(UUID id, @Valid Player player) {
    var gameOpt = gameRepository.findGameById(id);
    if (gameOpt.isEmpty()) {
      return Optional.empty();
    }

    var game = gameOpt.get();
    authRepository.findPlayerByNameAndGameId(player.name(), game.id())
        .ifPresent(p -> {
          throw new IllegalStateException(
              "Player with name " + player.name() + " already exists in game");
        });

    var runner = gameRunnerRegistry
        .getOrCreate(id);
    var state = runner
        .getCurrentState();
    if (state.phase().getKind() != GamePhaseKind.WAITING_FOR_PLAYERS) {
      throw new IllegalStateException("Game is already running or finished");
    }

    var playerKey = generateFreshKey();
    var ephemeralPlayer = new EphemeralPlayer(player.name(), game.id(), playerKey, false);
    authRepository.save(game, ephemeralPlayer);

    runner.tell(new GameCommand.AddPlayer(game.id(), ephemeralPlayer));

    return Optional.of(new GameJoinResponse(playerKey));

  }

  public void beginWarmup(Game game) {
    var runner = gameRunnerRegistry
        .getOrCreate(game.id());

    runner.tell(new GameCommand.StartWarmup(game.id()));
  }
}
