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
import io.micronaut.http.sse.Event
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.developerden.codosseum.dto.*
import org.developerden.codosseum.event.GameEvent
import org.developerden.codosseum.event.RoundStartEvent
import org.developerden.codosseum.event.SyncEvent
import org.developerden.codosseum.mode.GameModeType
import org.developerden.codosseum.model.GamePhase
import org.developerden.codosseum.service.game.event.SseEventSink
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.CopyOnWriteArrayList

@MicronautTest
class GameFlowSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient http

    @Inject
    SseEventSink eventSink

    // has to be thread safe as events come from another thread
    List<GameEvent> eventsReceived = new CopyOnWriteArrayList<>()


    void setup() {
        eventsReceived.clear()
        eventSink.allEvents().subscribe(new BaseSubscriber<Event<GameEvent>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(Long.MAX_VALUE)
            }

            @Override
            protected void hookOnNext(Event<GameEvent> value) {
                eventsReceived.add(value.data)
            }
        })
    }

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
                        .header("Authorization", "Game $key"), Void)

        then:
        getResponse.status.code == 204

        and: "wait until both events arrive"
        def conditions = new PollingConditions(timeout: 6, initialDelay: 0.1, delay: 0.1)
        conditions.eventually {
            assert eventsReceived.size() >= 2
            assert eventsReceived[0] instanceof SyncEvent
            assert (eventsReceived[0] as SyncEvent).state() == GamePhase.WARMUP
            assert eventsReceived[1] instanceof RoundStartEvent
        }


        def infoResponse = http.toBlocking().exchange("/games/${gameId}", GameInfo)
        then:
        infoResponse.status.code == 200
        def info = infoResponse.body()
        info.id() == gameId
        info.state() == GamePhase.IN_PROGRESS
        info.players().allPlayers().count() == 1

    }
}
