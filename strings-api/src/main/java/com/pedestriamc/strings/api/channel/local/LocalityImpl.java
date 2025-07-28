package com.pedestriamc.strings.api.channel.local;

import org.jetbrains.annotations.NotNull;

public class LocalityImpl<W> implements Locality<W> {

    private final W w;

    LocalityImpl(@NotNull W w) {
        this.w = w;
    }

    @Override
    public @NotNull W get() {
        return w;
    }
}
