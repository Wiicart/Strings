package com.pedestriamc.strings.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Class used to register the StringsAPI instance.
 */
public final class APIRegistrar {

    private APIRegistrar() {}

    public static void register(@NotNull final StringsAPI api, final JavaPlugin plugin, final UUID uuid) throws IllegalStateException, SecurityException {
        StringsProvider.register(api, uuid);
        Bukkit.getServer().getServicesManager().register(StringsAPI.class, api, plugin, ServicePriority.Highest);
        Bukkit.getLogger().info("[Strings] Strings API loaded.");
    }

    public static void unregister(final UUID uuid) throws IllegalStateException, SecurityException {
        Bukkit.getServer().getServicesManager().unregister(StringsAPI.class, StringsProvider.get());
        StringsProvider.unregister(uuid);
        Bukkit.getLogger().info("[Strings] Strings API unloaded.");
    }

    public static boolean isAPIUsed() {
        return StringsProvider.isUsed();
    }

}
