/*
 * SPDX-FileCopyrightText: 2025 Alexander Wood (BristerMitten)
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
 */

package org.developerden.codosseum;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest(startApplication = false)
class OpenApiGeneratedTest {

  @Test
  void buildGeneratesOpenApi(ResourceLoader resourceLoader) {
    assertTrue(resourceLoader.getResource("META-INF/swagger/codosseum-0.0.yml").isPresent());
  }
}
