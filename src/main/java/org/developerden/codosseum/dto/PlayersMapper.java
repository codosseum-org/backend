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

package org.developerden.codosseum.dto;

import org.developerden.codosseum.model.GamePlayers;
import org.developerden.codosseum.model.player.EphemeralPlayer;
import org.developerden.codosseum.model.player.GamePlayer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    componentModel = "jsr330"
)
public interface PlayersMapper {

  @Mapping(target = "name", source = "player.name")
  Player toDto(EphemeralPlayer player);

  @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.COMPILE_ERROR)
  @SubclassMapping(target = Player.class, source = EphemeralPlayer.class)
  Player toDto(GamePlayer player);

  @Mapping(target = "players", source = "others")
  Players toDto(GamePlayers gamePlayers);

  EphemeralPlayer toEphemeral(GamePlayer player);

}
