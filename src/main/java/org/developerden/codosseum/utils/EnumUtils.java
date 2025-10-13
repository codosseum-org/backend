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

package org.developerden.codosseum.utils;

import java.util.Collection;
import java.util.SplittableRandom;

public class EnumUtils {

  private static final SplittableRandom RANDOM = new SplittableRandom();

  /**
   * Picks a random enum value from the given enum type, optionally including only the specified values.
   *
   * @param enumType  the enum type to pick from.
   * @param including the enum values to include, or an empty collection to include all values.
   * @param <E>       the enum type.
   * @return a random enum value from the given enum type.
   */
  public static synchronized <E extends Enum<E>> E random(Class<E> enumType, Collection<E> including) {
    if (including.isEmpty()) {
      int index = RANDOM.nextInt(0, enumType.getEnumConstants().length);
      return enumType.getEnumConstants()[index];
    }

    return CollectionUtils.pickRandom(including);
  }
}
