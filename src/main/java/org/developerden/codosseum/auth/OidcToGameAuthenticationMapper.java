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

import com.nimbusds.jwt.JWT;
import io.micronaut.context.annotation.Primary;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.DefaultJwtAuthenticationFactory;
import io.micronaut.security.token.jwt.validator.JwtAuthenticationFactory;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
@Primary
public class OidcToGameAuthenticationMapper implements JwtAuthenticationFactory {

  private final JwtAuthenticationFactory mainFactory;

  public OidcToGameAuthenticationMapper(DefaultJwtAuthenticationFactory mainFactory) {
    this.mainFactory = mainFactory;
  }


  @Override
  public Optional<Authentication> createAuthentication(JWT token) {
    return mainFactory.createAuthentication(token)
        .map(auth -> auth);
  }
}
