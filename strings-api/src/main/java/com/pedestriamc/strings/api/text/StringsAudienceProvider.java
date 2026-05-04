package com.pedestriamc.strings.api.text;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface StringsAudienceProvider {

    /**
     * Provides an Audience of all players on a server, and the console.
     * @return An Audience.
     */
    @NotNull Audience all();

    /**
     * Provides an Audience of the console.
     * @return The Audience.
     */
    @NotNull Audience console();
}
