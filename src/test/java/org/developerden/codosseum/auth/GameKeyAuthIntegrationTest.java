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


package org.developerden.codosseum.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.developerden.codosseum.dto.GameCreateRequest;
import org.developerden.codosseum.dto.GameCreateResponse;
import org.developerden.codosseum.dto.GameSettingsBuilder;
import org.developerden.codosseum.dto.Player;
import org.developerden.codosseum.dto.user.User;
import org.developerden.codosseum.mode.GameModeType;
import org.junit.jupiter.api.Test;

@MicronautTest
class GameKeyAuthIntegrationTest {

  private static final URI SELF_URI = UriBuilder.of("/users").path("@self").build();
  @Inject
  @Client("/")
  HttpClient client;


  @Test
  void allows_with_valid_game_key() {
    HttpResponse<GameCreateResponse> testPlayer =
        client.toBlocking().exchange(HttpRequest.POST("/games",
            new GameCreateRequest(
                GameSettingsBuilder.builder().allowedGameModes(List.of(GameModeType.FASTEST))
                    .build(),
                new Player("test player")
            )
        ), GameCreateResponse.class);

    String key = testPlayer.body().adminKey();

    HttpResponse<User> response = client.toBlocking().exchange(
        HttpRequest.GET(SELF_URI)
            .header(HttpHeaders.AUTHORIZATION, "Game " + key),
        User.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("test player", response.body().name());
    assertEquals(testPlayer.body().id(), response.body().gameId());
  }

  @Test
  void disallows_with_valid_game_key() {
    String key = UUID.randomUUID().toString();

    HttpClientResponseException e =
        assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(
            HttpRequest.GET(SELF_URI)
                .header(HttpHeaders.AUTHORIZATION, "Game " + key)));

    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
  }


  @Test
  void unauthorized_without_token() {
    HttpClientResponseException e = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(HttpRequest.GET(SELF_URI), String.class));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
  }
}
