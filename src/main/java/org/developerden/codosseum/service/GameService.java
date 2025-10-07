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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameInfo;
import org.developerden.codosseum.dto.GameSettings;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.GamePlayers;
import org.developerden.codosseum.repository.GameRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class GameService {
    private final GameRepository gameRepository;

    public @Inject GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    private String generateAdminKey() {
        return UUID.randomUUID().toString();
    }

    public GameCreateResponse createGame(GameCreateRequest request) {

        var game = new Game(UUID.randomUUID(), generateAdminKey(), request.settings(), new GamePlayers(
                request.player(),
                new HashSet<>()
        ));

        gameRepository.insertGame(game);

        return new GameCreateResponse(game.adminKey(), game.id());
    }

    public GameInfo updateGame(String gameId, GameSettings settings) {
        throw new UnsupportedOperationException();
    }

    public void deleteGame(String gameId) {
        throw new UnsupportedOperationException();
    }

    public Optional<GameInfo> getGame(UUID id) {
        var game = gameRepository.findGameById(id);

        if(game == null) {
            return Optional.empty();
        }

        return Optional.of(game)
                .map(game -> new GameInfo(
                        game.settings(),
                        game.id(),
                ))
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
