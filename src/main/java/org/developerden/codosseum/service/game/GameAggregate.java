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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.model.GameState;
import org.developerden.codosseum.model.GameStateBuilder;
import org.developerden.codosseum.model.phase.GamePhaseKind;
import org.developerden.codosseum.model.phase.InProgressPhase;
import org.developerden.codosseum.model.phase.WarmupPhase;
import org.developerden.codosseum.service.game.effect.SideEffect;
import org.developerden.codosseum.service.game.event.InternalGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final String KEY_START_RETRY = "warmup->await-challenge";
  private static final String KEY_ROUND_OVER = "round->over";
  private static final Logger log = LoggerFactory.getLogger(GameAggregate.class);
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
  private void requirePhase(GamePhaseKind gamePhase) {
    if (gameState.phase().getKind() != gamePhase) {
      throw new IllegalStateException(
          "Game is not in required phase: " + gamePhase + ", current phase: " + gameState.phase());
    }
  }

  private <P extends org.developerden.codosseum.model.phase.GamePhase> P requirePhase(
      Class<P> phaseClass) {
    if (!phaseClass.isInstance(gameState.phase())) {
      throw new IllegalStateException(
          "Game is not in required phase: " + phaseClass.getSimpleName() + ", current phase: " +
              gameState.phase());
    }
    return phaseClass.cast(gameState.phase());
  }

  private Decision decide(GameCommand cmd) {
    if (!cmd.gameId().equals(gameId)) {
      throw new IllegalArgumentException("Command gameId does not match aggregate gameId");
    }

    return switch (cmd) {
      case GameCommand.CreateGame(var id, var creator) -> {
        requirePhase(GamePhaseKind.WAITING_FOR_PLAYERS);
        yield Decision.pure(
            new InternalGameEvent.GameCreated(gameId, creator)
        );

      }
      case GameCommand.StartGame(var id) -> {
        requirePhase(GamePhaseKind.WARMUP);

        if (getGameState().currentChallengeInfo() == null) {
          log.warn("Cannot start game {}, no challenge set, retrying...", gameId);
          yield Decision.empty().withEffects(
              new SideEffect.ScheduleAfter(KEY_START_RETRY, Duration.ofMillis(250),
                  new GameCommand.StartGame(gameId))
          );
        }
        yield startRound(1);
      }
      case GameCommand.AddPlayer(var id, var player) -> {
        requirePhase(GamePhaseKind.WAITING_FOR_PLAYERS);
        yield Decision.pure(new InternalGameEvent.PlayerJoined(gameId, player));
      }

      case GameCommand.StartWarmup(var id) -> {
        requirePhase(GamePhaseKind.WAITING_FOR_PLAYERS);
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
        var phase = requirePhase(InProgressPhase.class);

        var nextRound = phase.currentRound() + 1;

        yield startRound(nextRound);
      }
      case GameCommand.EndRound endRound -> {
        var phase = requirePhase(InProgressPhase.class);
        // For now, ending a round is a no-op
        yield Decision.pure(
            new InternalGameEvent.RoundEnded(gameId, phase.currentRound())
        );
      }
    };
  }


  private Decision startRound(int roundNumber) {
    var length = getRoundLength();
    ChallengeInfo challenge = Objects.requireNonNull(
        getGameState().currentChallengeInfo(),
        "Cannot start round without a challenge set"
    );
    return Decision.pure(
        new InternalGameEvent.RoundStarted(gameId, challenge, roundNumber, length)
    ).withEffects(
        new SideEffect.ScheduleAfter(
            KEY_ROUND_OVER,
            length,
            new GameCommand.EndRound(gameId)
        )
    );
  }

  private Duration getRoundLength() {
    // TODO: make configurable
    return Duration.ofMinutes(5);
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
      case InternalGameEvent.PlayerJoined(var gameId, var player) ->
          state.updatePlayers(players -> players
              .builder()
              .addOthers(player).build());
      case InternalGameEvent.GameCreated(var gameId, var player) -> state.updatePlayers(players ->
          players.builder()
              .admin(player)
              .addOthers(player)
              .build()
      );
      case InternalGameEvent.WarmupStarted(var gameId, var duration) -> GameStateBuilder.from(state)
          .withPhase(new WarmupPhase(
              state.players(),
              Instant.now().plus(duration)
          ));

      case InternalGameEvent.ChallengeSet challengeSet -> GameStateBuilder.from(state)
          .withCurrentChallengeInfo(challengeSet.challengeInfo());
      case InternalGameEvent.RoundEnded roundEnded -> state; // TODO: implement round end logic

      case InternalGameEvent.RoundStarted roundStarted -> GameStateBuilder.from(state)
          .withCurrentChallengeInfo(roundStarted.challenge())
          .withPhase(new InProgressPhase(
              state.players(),
              roundStarted.challenge(),
              roundStarted.roundNumber()
          ));
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
