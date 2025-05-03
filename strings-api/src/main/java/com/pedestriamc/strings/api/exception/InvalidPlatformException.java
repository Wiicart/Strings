package com.pedestriamc.strings.api.exception;

import com.pedestriamc.strings.api.platform.Platform;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class InvalidPlatformException extends Exception {

    private final Platform platform;

    public InvalidPlatformException(@NotNull String message, Platform platform) {
        super(message);
        this.platform = platform;
    }

    public Platform getSystemPlatform() {
        return platform;
    }

}
