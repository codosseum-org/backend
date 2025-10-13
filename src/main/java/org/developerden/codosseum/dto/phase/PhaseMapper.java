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

package org.developerden.codosseum.dto.phase;

import jakarta.inject.Singleton;
import org.developerden.codosseum.model.phase.GamePhase;
import org.developerden.codosseum.model.phase.InProgressPhase;
import org.developerden.codosseum.model.phase.UndefinedPhase;
import org.developerden.codosseum.model.phase.WaitingForPlayersPhase;
import org.developerden.codosseum.model.phase.WarmupPhase;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

/**
 * MapStruct Mapper for converting game phases to their API DTO representations.
 */
@Mapper(
    componentModel = "jsr330"
)
public interface PhaseMapper {

  ApiUndefinedPhase toDto(UndefinedPhase gamePhase);

  ApiWaitingForPlayersPhase toDto(WaitingForPlayersPhase phase);

  ApiWarmupPhase toDto(WarmupPhase phase);

  ApiInProgressPhase toDto(InProgressPhase phase);

  @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.COMPILE_ERROR)
  @SubclassMapping(source = InProgressPhase.class, target = ApiInProgressPhase.class)
  @SubclassMapping(source = UndefinedPhase.class, target = ApiUndefinedPhase.class)
  @SubclassMapping(source = WaitingForPlayersPhase.class, target = ApiWaitingForPlayersPhase.class)
  @SubclassMapping(source = WarmupPhase.class, target = ApiWarmupPhase.class)
  ApiGamePhase toDto(GamePhase phase);

}
