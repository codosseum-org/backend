/*
 * SPDX-FileCopyrightText: 2023 JohnnyJayJay
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.developerden.codosseum.challenge;

import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import org.developerden.codosseum.challenge.validation.SpdxId;

@Introspected
public record Challenge(
    @Nullable
    Author author,

    @SpdxId
    @Nullable
    String license,

    @Nullable
    String language,

    @Nonnull
    @NotNull
    String title,

    @Nullable
    String difficulty,

    @Nullable
    Set<String> tags,

    @Nonnull
    @NotNull
    String text,

    @Nullable
    String inputFormat,

    @Nullable
    List<Test> examples,

    @Nonnull
    @Size(min = 1)
    List<Test> publicTests,

    @Nullable
    Solution solution

) {

  public record Author(
      @Nonnull
      @NotBlank
      String name,

      @Nullable
      List<Contact> contact
  ) {
    public record Contact(

        @Nonnull
        @NotBlank
        String type,

        @Nonnull
        @NotBlank
        String contact
    ) {

    }
  }

  public record Test(
      @Nullable
      String name,

      @Nonnull
      @NotNull
      List<String> in,

      @Nonnull
      @NotNull
      List<String> out
  ) {

  }

  public record Solution(

      // Custom validator
      @Nonnull
      String language,

      // Custom validator
      @Nonnull
      String file
  ) {

  }
}
