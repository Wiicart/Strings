package com.pedestriamc.strings.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Static StringsAPI getter
 */
public final class StringsProvider {

    static final short VERSION = 5;

    private static StringsAPI api;
    private static UUID apiUuid;
    private static boolean invoked = false;

    // Class should not be instantiated.
    private StringsProvider() {}

    /**
     * Provides an instance of the StringsAPI.
     * @return An instance of the StringsAPI.
     */
    public static StringsAPI get() {
        if(api == null) {
            throw new IllegalStateException("Strings API not initialized.");
        }
        invoked = true;
        return StringsProvider.api;
    }

    /**
     * For internal use only.
     * @hidden
     * @param api API Implementation
     * @param plugin Strings plugin
     */
    public static void register(@NotNull final StringsAPI api, final JavaPlugin plugin, final UUID uuid) throws IllegalStateException, SecurityException {
        if(StringsProvider.api != null) {
            throw new IllegalStateException("StringsProvider already initialized.");
        }
        if(!StringsProvider.class.getClassLoader().equals(api.getClass().getClassLoader())) {
            throw new SecurityException("Unauthorized attempt to load StringsAPI.");
        }
        apiUuid = uuid;
        StringsProvider.api = api;
        Bukkit.getServer().getServicesManager().register(StringsAPI.class, StringsProvider.api, plugin, ServicePriority.Highest);
        Bukkit.getLogger().info("[Strings] Strings API loaded.");
    }

    /**
     * For internal use only.
     * @hidden
     * @param uuid UUID from Strings
     */
    public static void unregister(final UUID uuid) throws IllegalStateException, SecurityException {
        if(StringsProvider.api == null) {
            throw new IllegalStateException("StringsProvider uninitialized.");
        }
        if(apiUuid != uuid) {
            throw new SecurityException("Unregister method called with unauthorized UUID.");
        }
        StringsProvider.api = null;
        Bukkit.getLogger().info("[Strings] Strings API unloaded.");
    }

    /**
     * Provides a short of the API version.
     * @return A short of the API version.
     */
    public static short getVersion() {
        return VERSION;
    }

    public static boolean isUsed() {
        return invoked;
    }
}
