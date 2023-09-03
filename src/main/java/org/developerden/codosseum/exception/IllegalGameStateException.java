/*
 * SPDX-FileCopyrightText: 2023 JohnnyJayJay
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.developerden.codosseum.exception;

import java.util.Set;
import java.util.stream.Collectors;
import org.developerden.codosseum.model.GameState;

public class IllegalGameStateException extends RuntimeException {

  public IllegalGameStateException(String gameId, GameState state, Set<GameState> expected) {
    super("Game '" + gameId + "' is in state '" + state + "' but this action can only be "
        + "taken while in one of the following states: "
        + expected.stream().map(GameState::name).collect(Collectors.joining(", ")));
  }
}
