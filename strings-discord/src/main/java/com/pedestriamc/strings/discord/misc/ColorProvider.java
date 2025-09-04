package com.pedestriamc.strings.discord.misc;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.logging.Logger;

public final class ColorProvider {

    private ColorProvider() {}

    @NotNull
    public static Color parse(@NotNull String color, @NotNull Color fallback, @NotNull Logger logger) {
        try {
            return Color.decode(color);
        } catch(NumberFormatException e) {
            logger.warning("Could not parse color: " + color);
            return fallback;
        }
    }
}
