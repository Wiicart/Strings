package com.pedestriamc.strings.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Class used to register the StringsAPI instance.
 */
public final class APIRegistrar {

    private APIRegistrar() {}

    public static void register(@NotNull final StringsAPI api, final JavaPlugin plugin, final UUID uuid) throws IllegalStateException, SecurityException {
        StringsProvider.register(api, plugin, uuid);
    }

    public static void unregister(final UUID uuid) throws IllegalStateException, SecurityException {
        StringsProvider.unregister(uuid);
    }

    public static boolean isAPIUsed() {
        return StringsProvider.isUsed();
    }

}
