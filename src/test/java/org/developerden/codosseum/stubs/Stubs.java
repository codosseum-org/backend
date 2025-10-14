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

package org.developerden.codosseum.stubs;

import java.util.List;
import org.developerden.codosseum.challenges.client.model.Author;
import org.developerden.codosseum.challenges.client.model.ChallengeInfo;
import org.developerden.codosseum.challenges.client.model.Contact;
import org.developerden.codosseum.challenges.client.model.Difficulty;
import org.developerden.codosseum.challenges.client.model.Example;
import org.developerden.codosseum.challenges.client.model.Solution;
import org.developerden.codosseum.challenges.client.model.Test;

public class Stubs {

  public static ChallengeInfo fakeChallengeInfo() {
    return new ChallengeInfo(
        "empty-schema",
        new Author(
            "Author",
            List.of(
                new Contact(
                    "Author Contact Type",
                    "Author Contact Value"
                )
            )
        ),
        "License",
        "Language",
        "Test Challenge",
        Difficulty.EASY,
        List.of("tag1", "tag2"),
        "This is a test challenge.",
        "Input Format",
        List.of(new Example(
            List.of("Example Input"),
            List.of("Example Output")
        )),
        List.of(new Test(
            "Test",
            List.of("Test Input"),
            List.of("Test Output"
            ))),
        new Solution(
            "Solution Language",
            "solution file"
        )
    );
  }
}
