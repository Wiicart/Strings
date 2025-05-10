package com.pedestriamc.strings.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.*;

import java.util.UUID;

/**
 * Static StringsAPI getter
 */
public final class StringsProvider {

    private static StringsAPI api;
    private static UUID apiUuid;
    private static boolean invoked = false;
    private static StringsModeration moderation;

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
     * Provides the StringsModeration instance, offering some moderation methods.
     * @throws IllegalStateException Check if StringsModeration is available by checking if the plugin is enabled.
     * @return The StringsModeration implementation.
     */
    public static StringsModeration getModeration() {
        if(moderation == null) {
            throw new IllegalStateException("Strings Moderation is not available.");
        }
        invoked = true;
        return StringsProvider.moderation;
    }

    /**
     * Tells if the Strings plugin is loaded, and if the API is registered.
     * @return A boolean.
     */
    public static boolean isEnabled() {
        return api != null;
    }

    /**
     * Tells if the get() method has been invoked.
     * @return A boolean.
     */
    public static boolean isUsed() {
        return invoked;
    }

    /**
     * For internal use only.
     * Registers the StringsAPI implementation
     * @hidden
     * @param api API Implementation
     * @param plugin Strings plugin
     */
    @Internal
    static void register(@NotNull final StringsAPI api, final JavaPlugin plugin, final UUID uuid) throws IllegalStateException, SecurityException {
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
     * Unregisters the StringsAPI implementation
     * @hidden
     * @param uuid UUID from Strings
     */
    @Internal
    static void unregister(final UUID uuid) throws IllegalStateException, SecurityException {
        if(StringsProvider.api == null) {
            throw new IllegalStateException("StringsProvider uninitialized.");
        }
        if(apiUuid != uuid) {
            throw new SecurityException("Unregister method called with unauthorized UUID.");
        }
        StringsProvider.api = null;
        Bukkit.getLogger().info("[Strings] Strings API unloaded.");
    }

    @Internal
    static void registerModeration(StringsModeration moderation) {
        StringsProvider.moderation = moderation;
    }

    @Internal
    static void unregisterModeration() {
        StringsProvider.moderation = null;
    }

}
