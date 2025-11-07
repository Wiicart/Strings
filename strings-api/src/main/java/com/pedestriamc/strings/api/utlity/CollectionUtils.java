package com.pedestriamc.strings.api.utlity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings("unused")
public final class CollectionUtils {

    private CollectionUtils() {}

    @Nullable
    public static <T> T first(@NotNull Collection<T> collection) {
        return collection.stream().findFirst().orElse(null);
    }

    @Nullable
    public static <T> T any(@NotNull Collection<T> collection) {
        return collection.stream().findAny().orElse(null);
    }
}
