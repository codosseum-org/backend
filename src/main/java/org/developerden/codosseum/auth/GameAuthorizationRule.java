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

package org.developerden.codosseum.auth;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.AbstractSecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.security.token.RolesFinder;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;
import jakarta.inject.Singleton;
import java.util.Arrays;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
public class GameAuthorizationRule extends AbstractSecurityRule<HttpRequest<?>> {

  protected GameAuthorizationRule(RolesFinder rolesFinder) {
    super(rolesFinder);
  }

  @Override
  public Publisher<SecurityRuleResult> check(@Nullable HttpRequest<?> request,
                                             @Nullable Authentication authentication) {
    RouteMatch<?> routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class)
        .orElse(null);
    if (routeMatch instanceof MethodBasedRouteMatch<?, ?> methodMatch
        && methodMatch.hasAnnotation(GameAuthorized.class)) {
      GameRole[] roles = methodMatch
          .getValue(GameAuthorized.class, GameRole[].class).orElseThrow();
      String gameId = (String) routeMatch.getVariableValues().get("game");
      if (gameId == null) {
        throw new AssertionError(
            "GameAuthorized annotation used without game ID parameter in path");
      }

      if (authentication == null || !matchesGameId(authentication, gameId)) {
        return Mono.just(SecurityRuleResult.REJECTED);
      }

      boolean hasRequiredRole =
          Arrays.stream(roles).map(GameRole::name).anyMatch(authentication.getRoles()::contains);

      return Mono.just(hasRequiredRole ? SecurityRuleResult.ALLOWED : SecurityRuleResult.REJECTED);
    }
    return Mono.just(SecurityRuleResult.UNKNOWN);
  }

  private boolean matchesGameId(Authentication authentication, String gameId) {
    return gameId.equals(authentication.getAttributes().get("activeGameId"));
  }
}
