package com.pedestriamc.strings.api.settings;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Internal
public interface Key<T> {

    @NotNull
    String key();

    @NotNull
    T defaultValue();

    /**
     * Signifies where a Key is from
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @interface From {
        String path();
    }
}
