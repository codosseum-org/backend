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

package org.developerden.codosseum.service;

import jakarta.inject.Singleton;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;

@Singleton
public class GameService {

  public GameCreateResponse createGame(GameCreateRequest request) {
    throw new UnsupportedOperationException();
  }

  public GameInfo updateGame(String gameId, GameSettings settings) {
    throw new UnsupportedOperationException();
  }

  public void deleteGame(String gameId) {
    throw new UnsupportedOperationException();
  }

  public GameInfo getGame(String id) {
    throw new UnsupportedOperationException();
  }

  public void startGame(String gameId) {
    // check before if game is in warmup state
    initiateNextRound(gameId);
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

}
