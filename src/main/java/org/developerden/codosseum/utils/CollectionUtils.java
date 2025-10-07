package org.developerden.codosseum.utils;

import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

public class CollectionUtils {
    public static <E> E pickRandom(@NotEmpty Collection<E> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Collection is empty");
        }

        return collection.stream().skip((int) (Math.random() * collection.size())).findFirst()
                .orElseThrow();
    }
}
