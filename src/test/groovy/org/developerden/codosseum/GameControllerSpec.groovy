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
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.developerden.codosseum.dto.*
import org.developerden.codosseum.mode.GameModeType
import spock.lang.Specification

@MicronautTest
class GameControllerSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient http

    def "GET /games/{id} not found returns 404"() {
        given:
        def id = UUID.randomUUID()

        when:
        http.toBlocking().exchange("/games/${id}", String)

        then:
        def e = thrown(HttpClientResponseException)
        e.status.code == 404
    }

    def "POST /games returns 201 and has a valid body"() {
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
    }

    def "POST /games returns 201 and GET /games/{id} returns 200"() {
        when:
        def response = http.toBlocking().exchange(HttpRequest.POST("/games",
                new GameCreateRequest(
                        GameSettingsBuilder.builder().allowedGameModes(List.of(GameModeType.FASTEST)).build(),
                        new Player("test player")
                )
        ), GameCreateResponse)

        then:
        response.status.code == 201
        def body = response.body()

        body.id() != null
        def id = body.id()

        when:
        def getResponse = http.toBlocking().exchange("/games/${id}", GameInfo)
        then:
        getResponse.status.code == 200
        def info = getResponse.body()
        info.id() == id
        info.players().allPlayers().count() == 1
        info.settings().allowedGameModes() == [GameModeType.FASTEST]
    }


}
