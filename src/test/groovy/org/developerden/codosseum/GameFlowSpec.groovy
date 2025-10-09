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

package org.developerden.codosseum

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.developerden.codosseum.dto.GameCreateRequest
import org.developerden.codosseum.dto.GameCreateResponse
import org.developerden.codosseum.dto.GameSettingsBuilder
import org.developerden.codosseum.dto.Player
import org.developerden.codosseum.mode.GameModeType
import spock.lang.Specification

@MicronautTest
class GameFlowSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient http

    def "Warmup phase starts correctly"() {
        when:
        def response = http.toBlocking().exchange(HttpRequest.POST("/games",
                new GameCreateRequest(
                        GameSettingsBuilder.builder().allowedGameModes(List.of(GameModeType.FASTEST)).build(),
                        new Player("test player")
                )
        ), GameCreateResponse)

        then:
        response.status.code == 201
        response.body().id() != null

        def gameId = response.body().id()
        def key = response.body().adminKey()

        when:
        def getResponse = http.toBlocking()
                .exchange(HttpRequest.POST("/games/${gameId}/warmup", null)
                        .header("Authorization", key), Void)

        then:
        getResponse.status.code == 204


    }
}
