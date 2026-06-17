package com.pedestriamc.strings.integration.discordsrv;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.utlity.Integration;
import org.jetbrains.annotations.NotNull;

/**
 * Loads the DiscordSRV integration if DiscordSRV is available.
 */
public final class DiscordSRVIntegration implements Integration {

    private final Strings strings;

    public DiscordSRVIntegration(@NotNull Strings strings) {
        this.strings = strings;
    }

    /**
     * Object of type {@code StringsChatHook}.<br/>
     * Not directly referenced to avoid NoClassDef error if DiscordSRV is not present.
     */
    private Object hook;

    @Override
    public void load(@NotNull StringsPlatform plugin) {
        try {
            if (strings.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
                hook = new com.pedestriamc.strings.integration.discordsrv.StringsChatHook(strings);

                strings.eventManager().subscribe(hook);

                github.scarsz.discordsrv.DiscordSRV.getPlugin()
                        .getPluginHooks()
                        .add((github.scarsz.discordsrv.hooks.chat.ChatHook) hook);

                strings.info("DiscordSRV integration loaded.");
            }
        } catch (Exception e) {
            strings.warning("Failed to load DiscordSRV hook.");
            strings.warning(e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (hook != null) {
            strings.eventManager().unsubscribe(hook);

            github.scarsz.discordsrv.DiscordSRV.getPlugin()
                    .getPluginHooks()
                    .remove((github.scarsz.discordsrv.hooks.chat.ChatHook) hook);
        }
    }

}
