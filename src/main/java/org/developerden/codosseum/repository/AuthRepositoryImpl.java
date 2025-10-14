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

import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.GamePlayer;
import org.developerden.codosseum.model.player.RegisteredUser;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

  private final ConcurrentMap<PlayerKey, EphemeralPlayer> players = new ConcurrentHashMap<>();
  private final ConcurrentMap<UUID, RegisteredUser> registeredUsers = new ConcurrentHashMap<>();

  @Override
  public Optional<EphemeralPlayer> findPlayerByGameKey(String gameKey) {
    return players.values().stream()
        .filter(player -> player.key().equals(gameKey))
        .findFirst(); // TODO: not very efficient
  }

  @Override
  public Iterable<GamePlayer> allPlayers() {
    return new HashSet<>(players.values());
  }

  @Override
  public void save(Game game, EphemeralPlayer player) {
    players.put(new PlayerKey(game.id(), player.name()), player);
  }

  @Override
  public Optional<EphemeralPlayer> findPlayerByNameAndGameId(String name, UUID id) {
    return Optional.ofNullable(players.get(new PlayerKey(id, name)));
  }

  @Override
  public Optional<RegisteredUser> findRegisteredUserById(UUID id) {
    return Optional.ofNullable(registeredUsers.get(id));
  }

  @Override
  public void saveRegisteredUser(RegisteredUser user) {
    registeredUsers.put(user.id(), user);
  }


  public Optional<RegisteredUser> findOrCreateRegisteredUser(String provider, String subject,
                                                             String displayName) {
    return registeredUsers.values().stream()
        .filter(user -> user.provider().equals(provider) && user.subject().equals(subject))
        .findFirst()
        .or(() -> {
          RegisteredUser newUser =
              new RegisteredUser(UUID.randomUUID(), provider, subject, displayName);
          registeredUsers.put(newUser.id(), newUser);
          return Optional.of(newUser);
        });
  }

  private record PlayerKey(UUID gameId, String playerName) {
  }
}
