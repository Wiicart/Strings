package com.pedestriamc.strings.moderation.impl;

import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.StringsProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

// Reflection-based registrar, based off LuckPerms implementation.
public final class Registrar {

    private Registrar() {}

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = StringsProvider.class.getDeclaredMethod("registerModeration", StringsModeration.class);
            REGISTER.setAccessible(true);

            UNREGISTER = StringsProvider.class.getDeclaredMethod("unregisterModeration");
            UNREGISTER.setAccessible(true);
        } catch(NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register(@NotNull StringsModeration moderation, @NotNull com.pedestriamc.strings.moderation.StringsModeration plugin) {
        try {
            REGISTER.invoke(null, moderation);
            plugin.getServer().getServicesManager().register(StringsModeration.class, moderation, plugin, ServicePriority.Highest);
            plugin.getLogger().info("[StringsModeration] Registered with StringsAPI");
        } catch(Exception e) {
            plugin.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            plugin.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

    public static void unregister(@NotNull com.pedestriamc.strings.moderation.StringsModeration plugin) {
        try {
            UNREGISTER.invoke(null);
            plugin.getServer()
                    .getServicesManager()
                    .unregisterAll(plugin);
            plugin.getLogger().info("[StringModeration] Unregistered from StringsAPI");
        } catch(Exception e) {
            plugin.getLogger().warning("[StringsModeration] Failed to unregister StringsModeration API");
            plugin.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

}
