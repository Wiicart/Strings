package com.pedestriamc.strings.api.utlity;

import com.pedestriamc.strings.api.StringsPlatform;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an integration with another plugin
 */
public interface Integration {


    /**
     * Loads the integration if supported in the given environment.
     * @param strings The Strings plugin.
     */
    void load(@NotNull StringsPlatform strings);

    /**
     * Disables this integration.
     */
    void disable();

}
