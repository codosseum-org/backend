package org.developerden.codosseum.controller.binder;

import io.micronaut.core.bind.ArgumentBinder;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.web.router.UriRouteMatch;
import jakarta.inject.Singleton;
import org.developerden.codosseum.model.Game;
import org.developerden.codosseum.repository.GameRepository;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class GameParamBinder implements TypedRequestArgumentBinder<Game> {
    private final GameRepository gameRepository;

    public GameParamBinder(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Argument<Game> argumentType() {
        return Argument.of(Game.class);
    }

    @Override
    public BindingResult<Game> bind(ArgumentConversionContext<Game> context, HttpRequest<?> source) {
        // Check if the parameter has @GameParam annotation
        if (!context.getArgument().isAnnotationPresent(GameParam.class)) {
            return ArgumentBinder.BindingResult.UNSATISFIED;
        }

        // Get the path variable name from the annotation
        String pathVariableName = context.getArgument()
                .getAnnotation(GameParam.class)
                .stringValue()
                .orElse("id");

        // Extract the UUID from path variables using getParameters()
        String uuidStr = (String) source.getAttribute("micronaut.http.route.match", UriRouteMatch.class)
                .map(m -> m.getVariableValues().get(pathVariableName))
                .filter(x -> x instanceof String).orElse(null);



        if (uuidStr == null) {
            return ArgumentBinder.BindingResult.UNSATISFIED;
        }

        try {
            UUID gameId = UUID.fromString(uuidStr);
            Optional<Game> game = gameRepository.findGameById(gameId);

            if (game.isEmpty()) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Game not found");
            }

            return () -> game;
        } catch (IllegalArgumentException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid game ID format");
        }
    }
}