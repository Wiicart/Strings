package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class LocalityImpl<W> implements Locality<W> {

    private final W w;

    LocalityImpl(@NotNull W w) {
        this.w = w;
    }

    @Override
    public @NotNull W get() {
        return w;
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
