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

package org.developerden.codosseum.mode;

import jakarta.inject.Singleton;

@Singleton
public class GameModeFactoryImpl implements GameModeFactory {
  @Override
  public GameMode initForGame(String gameId) {
    return null;
  }

  @Override
  public GameMode fromType(GameModeType type) {
    return switch (type) {
      case FASTEST -> new FastestGameMode();
      default -> throw new UnsupportedOperationException("Unsupported game mode type: " + type);
    };
  }
}
