package com.pedestriamc.strings.integration;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.integration.discordsrv.DiscordSRVIntegration;
import org.jetbrains.annotations.NotNull;

public class Integrations {

    private final DiscordSRVIntegration discord;

    /**
     * Creates a new Instance and loads all integrations that are eligible for loading.
     * @param strings Strings
     */
    public Integrations(@NotNull Strings strings) {
        discord = new DiscordSRVIntegration(strings);
        discord.load(strings);
    }

    /**
     * Disables all integrations.
     */
    public void disableAll() {
        discord.disable();
    }

}
