package com.pedestriamc.strings.api.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// Provides access to the package-private StringsReloadEvent constructor for the Strings main class
public final class StringsReloader {

    private StringsReloader() {}

    @Contract(" -> new")
    public static @NotNull StringsReloadEvent createEvent() {
        return new StringsReloadEvent();
    }
}
