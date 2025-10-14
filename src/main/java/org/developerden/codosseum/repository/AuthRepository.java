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

package org.developerden.codosseum.repository;

import java.util.Optional;
import java.util.UUID;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.GamePlayer;
import org.developerden.codosseum.model.player.RegisteredUser;

/**
 * Repository for storing and retrieving authentication-related data.
 *
 * <p>There are 2 methods of authentication supported by Codosseum:
 * <ul>
 *   <li>Anonymous: When creating or joining a game, a player is assigned a name of their choosing, and a unique game key, which is used to authenticate them for the duration of the game. There is no long term persistence</li>
 *   <li>Registered: A user signs up by providing email and password, or uses OIDC (e.g. to sign up with GitHub).  This creates a long term user account, which can be used to create and join games. Users authenticated through this method can be in multiple games at once with the same key.</li>
 * </ul>
 *
 * <p>This type provides state management for both types of authentication.
 */
public interface AuthRepository {

  /**
   * Find a player by its game/admin key.
   *
   * @param gameKey The game/admin key.
   * @return The player, or null if not found.
   */
  Optional<EphemeralPlayer> findPlayerByGameKey(String gameKey);


  /**
   * Get all players in the repository.
   *
   * @return all players.
   */
  Iterable<GamePlayer> allPlayers();

  /**
   * Save a player to the repository.
   *
   * @param game   the game the player is in.
   * @param player the player to save.
   */
  void save(Game game, EphemeralPlayer player);

  /**
   * Find a player by its name and game id.
   *
   * @param name the name of the player.
   * @param id   the id of the game.
   * @return a player in the given game going by the given name, or empty if not found.
   */
  Optional<EphemeralPlayer> findPlayerByNameAndGameId(String name, UUID id);


  Optional<RegisteredUser> findRegisteredUserById(UUID id);

  void saveRegisteredUser(RegisteredUser user);

}
