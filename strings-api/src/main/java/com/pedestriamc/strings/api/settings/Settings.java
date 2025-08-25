package com.pedestriamc.strings.api.settings;

import com.pedestriamc.strings.api.StringsAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provides all plugin configuration values. All values are retrieved with {@link Option} enums.
 * Falls-back to default values if the option cannot be found.
 * Instance provided by {@link StringsAPI#getSettings()}
 */
public interface Settings {

    /**
     * Gets a boolean configuration value
     * @param option The Option
     * @return The value
     */
    boolean getBoolean(@NotNull Option.Bool option);

    /**
     * Gets a double configuration value
     * @param option The Option
     * @return The value
     */
    double getDouble(@NotNull Option.Double option);

    /**
     * Gets a float configuration value
     * @param option The Option
     * @return The value
     */
    float getFloat(@NotNull Option.Double option);

    /**
     * Gets a String configuration value
     * @param option The Option
     * @return The value
     */
    @NotNull
    String getString(@NotNull Option.Text option);

    /**
     * Gets a colored String configuration value
     * @param option The Option
     * @return The value
     */
    @NotNull
    String getColored(@NotNull Option.Text option);

    /**
     * Gets a String List configuration value
     * @param option The Option
     * @return The value
     */
    @NotNull
    List<String> getStringList(@NotNull Option.StringList option);

}
