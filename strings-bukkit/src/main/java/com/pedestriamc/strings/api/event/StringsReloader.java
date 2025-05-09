package com.pedestriamc.strings.api.event;

public final class StringsReloader {
    private StringsReloader() {}

    public static StringsReloadEvent createEvent() {
        return new StringsReloadEvent();
    }
}
