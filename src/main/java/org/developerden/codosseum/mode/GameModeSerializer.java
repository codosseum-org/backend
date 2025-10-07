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
    public @Nullable GameMode deserialize(@NonNull Decoder decoder, @NonNull DecoderContext context, @NonNull Argument<? super GameMode> type) throws IOException {
        return gameModeFactory.fromType(GameModeType.valueOf(decoder.decodeString().toUpperCase()));
    }

    @Override
    public void serialize(@NonNull Encoder encoder, @NonNull EncoderContext context, @NonNull Argument<? extends GameMode> type, @NonNull GameMode value) throws IOException {
        encoder.encodeString(value.getType().name().toLowerCase());
    }
}
