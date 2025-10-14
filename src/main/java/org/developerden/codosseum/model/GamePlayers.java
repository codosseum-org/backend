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

package org.developerden.codosseum.model;

import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.developerden.codosseum.model.player.GamePlayer;

/**
 * Internal model representing the players in a game.
 *
 * @param admin  the admin player
 * @param others the other players. For future proofing in the case of multi-admin games, this will also include the {@link #admin}
 */
@RecordBuilder()
@RecordBuilder.Options(
    useImmutableCollections = true,
    addSingleItemCollectionBuilders = true
)
public record GamePlayers(@Nullable GamePlayer admin, @Nonnull Set<GamePlayer> others) {

  public GamePlayersBuilder builder() {
    return GamePlayersBuilder.builder(this);
  }
}
