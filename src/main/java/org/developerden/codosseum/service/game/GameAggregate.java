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

package org.developerden.codosseum.service.game;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.model.GamePhase;
import org.developerden.codosseum.model.GamePlayersBuilder;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;
import org.developerden.codosseum.service.game.effect.SideEffect;
import org.developerden.codosseum.service.game.event.InternalGameEvent;

/**
 * Handles commands, produces events, and applies events to the game state.
 *
 * <p>There should be one instance of this class per game.
 *
 * @see GameState
 * @see GameCommand
 * @see InternalGameEvent
 */
public class GameAggregate {
  private static final Duration DEFAULT_WARMUP_DURATION = Duration.ofSeconds(5);
  private static final String KEY_WARMUP_TO_START = "warmup->start";

  private final UUID gameId;

  private final GameState gameState;

  public GameAggregate(UUID gameId, GameState gameState) {
    this.gameId = gameId;
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return gameState;
  }

  /**
   * Handle a command, producing events and side effects, and returning the next state of the aggregate.
   *
   * @param cmd the command to handle.
   * @return a result containing the events, side effects, and next state.
   */
  public Result handle(GameCommand cmd) {
    var decision = decide(cmd);
    var newState = applyAll(gameState, decision.events());

    var next = new GameAggregate(gameId, newState);

    return new Result(decision.events(), decision.effects(), next);
  }

  /**
   * Require that the game is in the given phase, throwing an exception if not.
   *
   * @param gamePhase the required phase.
   */
  private void requirePhase(GamePhase gamePhase) {
    if (gameState.phase() != gamePhase) {
      throw new IllegalStateException(
          "Game is not in required phase: " + gamePhase + ", current phase: " + gameState.phase());
    }
  }

  private Decision decide(GameCommand cmd) {
    if (!cmd.gameId().equals(gameId)) {
      throw new IllegalArgumentException("Command gameId does not match aggregate gameId");
    }

    return switch (cmd) {
      case GameCommand.CreateGame(var id, var creator) -> {
        requirePhase(GamePhase.WAITING_FOR_PLAYERS);
        yield Decision.pure(
            new InternalGameEvent.GameCreated(gameId, creator)
        );

      }
      case GameCommand.StartGame(var id) -> {
        requirePhase(GamePhase.WARMUP);

        yield Decision.pure(
            new InternalGameEvent.GameStarted(gameId)
        );
      }
      case GameCommand.AddPlayer(var id, var player) -> {
        requirePhase(GamePhase.WAITING_FOR_PLAYERS);
        yield Decision.pure(new InternalGameEvent.PlayerJoined(gameId, player));
      }

      case GameCommand.StartWarmup(var id) -> {
        requirePhase(GamePhase.WAITING_FOR_PLAYERS);
        // Emit warmup started and schedule transition to start after countdown
        yield Decision.pure(
            new InternalGameEvent.WarmupStarted(gameId, DEFAULT_WARMUP_DURATION)
        ).withEffects(
            new SideEffect.ScheduleAfter(
                KEY_WARMUP_TO_START,
                DEFAULT_WARMUP_DURATION,
                new GameCommand.StartGame(gameId)
            )
        );
      }
      case GameCommand.SetChallengeInfo(var id, var challenge) -> Decision.pure(
          new InternalGameEvent.ChallengeSet(gameId, challenge)
      );
      case GameCommand.StartRound(var id) -> {
        requirePhase(GamePhase.IN_PROGRESS);

        var nextRound = Math.max(gameState.currentRound() + 1, 1);
        var roundLength = Duration.ofMinutes(5); // TODO: make configurable / dynamic
        ChallengeInfo challenge = Objects.requireNonNull(getGameState().currentChallengeInfo(),
            "Cannot start round without a challenge set");
        yield Decision.pure(
            new InternalGameEvent.RoundStarted(gameId, challenge,
                nextRound, roundLength)
        ).withEffects(
            new SideEffect.ScheduleAfter(
                "round->over",
                roundLength,
                new GameCommand.EndRound(gameId)
            ));
      }
      case GameCommand.EndRound endRound -> {
        requirePhase(GamePhase.IN_PROGRESS);
        // For now, ending a round is a no-op
        yield Decision.pure(
            new InternalGameEvent.RoundEnded(gameId, gameState.currentRound())
        );
      }
    };
  }

  private GameState applyAll(GameState state, List<InternalGameEvent> events) {
    var newState = state;
    for (var event : events) {
      newState = apply(newState, event);
    }
    return newState;
  }

  private GameState apply(GameState state, InternalGameEvent event) {
    if (!event.gameId().equals(gameId)) {
      throw new IllegalArgumentException("Event gameId does not match aggregate gameId");
    }
    return switch (event) {
      case InternalGameEvent.PlayerJoined(var gameId, var player) -> GameStateBuilder.from(state)
          .withPlayers(
              GamePlayersBuilder.builder(state.players())
                  .addOthers(player)
                  .build()
          );
      case InternalGameEvent.GameCreated(var gameId, var player) -> GameStateBuilder.from(state)
          .withPlayers(
              GamePlayersBuilder.builder(state.players())
                  .admin(player)
                  .addOthers(player)
                  .build()
          );
      case InternalGameEvent.WarmupStarted(var gameId, var duration) -> GameStateBuilder.from(state)
          .withPhase(GamePhase.WARMUP);
      case InternalGameEvent.GameStarted(var gameId) -> GameStateBuilder.from(state)
          .withPhase(GamePhase.IN_PROGRESS);
      case InternalGameEvent.ChallengeSet challengeSet -> GameStateBuilder.from(state)
          .withCurrentChallengeInfo(challengeSet.challengeInfo());
      case InternalGameEvent.RoundEnded roundEnded -> state; // TODO: implement round end logic
      case InternalGameEvent.RoundStarted roundStarted -> GameStateBuilder.from(state)
          .withCurrentRound(roundStarted.roundNumber());
    };

  }

  /**
   * The result of deciding how to handle a command.
   *
   * @param events  the events that were produced.
   * @param effects the side effects that should be executed.
   */
  private record Decision(List<InternalGameEvent> events, List<SideEffect> effects) {
    static Decision empty() {
      return new Decision(List.of(), List.of());
    }

    static Decision pure(List<InternalGameEvent> events) {
      return new Decision(events, List.of());
    }

    static Decision pure(InternalGameEvent... events) {
      return new Decision(Arrays.asList(events), List.of());
    }

    public Decision withEffects(List<SideEffect> effects) {
      return new Decision(events, effects);
    }

    public Decision withEffects(SideEffect... effects) {
      return new Decision(events, Arrays.asList(effects));
    }
  }

  /**
   * The result of handling a command.
   *
   * @param events  the events that were produced
   * @param effects the side effects that should be executed
   * @param next    the next state of the aggregate
   */
  public record Result(List<InternalGameEvent> events, List<SideEffect> effects,
                       GameAggregate next) {
  }

}
