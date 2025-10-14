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


package org.developerden.codosseum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.developerden.codosseum.dto.user.User;
import org.junit.jupiter.api.Test;

@MicronautTest
class JwtAuthIT implements TestPropertyProvider {

  private static final URI SELF_URI = UriBuilder.of("/users").path("@self").build();
  @Inject
  @Client("/")
  HttpClient client;
  @Inject
  TokenGenerator tokenGenerator;

  @Override
  public Map<String, String> getProperties() {
    Map<String, String> p = new HashMap<>();
    p.put("micronaut.security.enabled", "true");
    p.put("micronaut.security.authentication", "idtoken"); // use ID Token cookie
    p.put("micronaut.security.oauth2.enabled", "false");   // no real IdP in tests
    p.put("micronaut.security.token.jwt.enabled", "true");
    p.put("micronaut.security.token.jwt.cookie.enabled", "true"); // cookie name defaults to JWT
    p.put("micronaut.security.token.jwt.signatures.secret.generator.secret",
        "please-change-test-secret");
    p.put("micronaut.security.token.roles-name", "roles"); // map 'roles' claim to authorities
    return p;
  }

  private String jwt(Map<String, Object> claims) {
    return tokenGenerator.generateToken(claims).orElseThrow();
  }

  @Test
  void allows_with_required_role() {
    // Match your route annotations: use "ADMIN" for @Secured("ADMIN") or "ROLE_ADMIN" for @Secured("ROLE_ADMIN")
    UUID userId = UUID.randomUUID();
    String token = jwt(Map.of("sub", userId.toString(), "name", "Test User"));
    HttpRequest<?> req = HttpRequest.GET(SELF_URI).cookie(Cookie.of("JWT", token));
    HttpResponse<User> resp = client.toBlocking().exchange(req, User.class);
    assertEquals(HttpStatus.OK, resp.getStatus());
    assertEquals(userId, resp.body().id());
    assertEquals("Test User", resp.body().name());
    assertNull(resp.body().gameId());
  }

  @Test
  void unauthorized_without_token() {
    HttpClientResponseException e = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(HttpRequest.GET(SELF_URI), String.class));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
  }
}
