package com.pedestriamc.strings.api.settings;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface Key<T> {

    @NotNull
    String key();

    @NotNull
    T defaultValue();

}
