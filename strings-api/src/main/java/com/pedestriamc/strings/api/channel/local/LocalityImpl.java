package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Internal
class LocalityImpl<W> implements Locality<W> {

    private final W w;
    private final String name;

    LocalityImpl(@NotNull W w, @NotNull String name) {
        this.w = w;
        this.name = name;
    }

    @Override
    public @NotNull W get() {
        return w;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if(object == null || getClass() != object.getClass()) return false;
        LocalityImpl<?> locality = (LocalityImpl<?>) object;
        return Objects.equals(w, locality.w);
    }
}
