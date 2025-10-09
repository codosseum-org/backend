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

package org.developerden.codosseum.service.game.effect;

import java.time.Duration;
import org.developerden.codosseum.service.game.GameCommand;

/**
 * Side effects that a GameAggregate requests the runner to perform.
 * Effects are executed by the {@code GameRunner} and are not part of state mutation.
 */
public sealed interface SideEffect permits SideEffect.ScheduleAfter, SideEffect.CancelScheduled {

  /**
   * Schedule a command to be sent to this game after the given delay.
   * The key is used for idempotency and cancellation/replacement.
   */
  record ScheduleAfter(String key, Duration delay, GameCommand command) implements SideEffect {}

  /**
   * Cancel a previously scheduled command by key.
   */
  record CancelScheduled(String key) implements SideEffect {}
}
