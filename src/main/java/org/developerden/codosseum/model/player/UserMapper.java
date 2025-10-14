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

package org.developerden.codosseum.model.player;

import org.developerden.codosseum.dto.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "jsr330")
public interface UserMapper {
  @Mapping(source = "gameId", target = "gameId")
  @Mapping(source = "name", target = "name")
  @Mapping(target = "id", ignore = true)
  User toDto(EphemeralPlayer ephemeralPlayer);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "displayName", target = "name")
  @Mapping(target = "gameId", ignore = true)
  User toDto(RegisteredUser registeredUser);

  @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.COMPILE_ERROR)
  @SubclassMapping(source = EphemeralPlayer.class, target = User.class)
  @SubclassMapping(source = RegisteredUser.class, target = User.class)
  User toDto(CodosseumUser user);
}
