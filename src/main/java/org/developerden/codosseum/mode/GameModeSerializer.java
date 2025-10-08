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

package org.developerden.codosseum.mode;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serde;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class GameModeSerializer implements Serde<GameMode> {
  private final @Inject GameModeFactory gameModeFactory;

  public GameModeSerializer(GameModeFactory gameModeFactory) {
    this.gameModeFactory = gameModeFactory;
  }

  @Override
  public @Nullable GameMode deserialize(@NonNull Decoder decoder, @NonNull DecoderContext context,
                                        @NonNull Argument<? super GameMode> type)
      throws IOException {
    return gameModeFactory.fromType(GameModeType.valueOf(decoder.decodeString().toUpperCase()));
  }

  @Override
  public void serialize(@NonNull Encoder encoder, @NonNull EncoderContext context,
                        @NonNull Argument<? extends GameMode> type, @NonNull GameMode value)
      throws IOException {
    encoder.encodeString(value.getType().name().toLowerCase());
  }
}
