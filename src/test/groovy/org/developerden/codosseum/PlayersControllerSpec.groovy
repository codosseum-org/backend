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
import org.developerden.codosseum.dto.*
import org.developerden.codosseum.mode.GameModeType
import spock.lang.Specification

@MicronautTest
class PlayersControllerSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient http

    def "Players can join game"() {
        given:
        def createResponse = http.toBlocking().exchange(HttpRequest.POST("/games",
                new GameCreateRequest(
                        GameSettingsBuilder.builder().allowedGameModes(List.of(GameModeType.FASTEST)).build(),
                        new Player("test player")
                )
        ), GameCreateResponse)
        def gameId = createResponse.body().id()

        when:
        def joinResponse = http.toBlocking().exchange(HttpRequest.POST("/games/${gameId}/players",
                new Player("second player")), GameJoinResponse)

        then:
        joinResponse.status.code == 200
        def joinBody = joinResponse.body()
        joinBody.key() != null

        when:
        def getResponse = http.toBlocking().exchange("/games/${gameId}", GameInfo)
        then:
        getResponse.status.code == 200
        def info = getResponse.body()
        info.id() == gameId
        info.players().allPlayers().count() == 2
        info.players().allPlayers().find { it.name() == "second player" } != null
        info.settings().allowedGameModes() == [GameModeType.FASTEST]

    }


}
