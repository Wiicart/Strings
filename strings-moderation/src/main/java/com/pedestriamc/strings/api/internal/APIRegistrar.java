package com.pedestriamc.strings.api.internal;

import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.StringsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.lang.reflect.Method;

// Reflection-based registrar, based off LuckPerms implementation.
public final class APIRegistrar {

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

    private APIRegistrar() {}

    public static void register(StringsModeration moderation, Plugin plugin) {
        try {
            REGISTER.invoke(null, moderation);
            plugin.getServer().getServicesManager().register(StringsModeration.class, moderation, plugin, ServicePriority.Highest);
            plugin.getLogger().info("[StringsModeration] Registered with StringsAPI");
        } catch(Exception e) {
            plugin.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            plugin.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

    public static void unregister() {
        try {
            UNREGISTER.invoke(null);
            Bukkit.getServer().getServicesManager().unregister(StringsModeration.class);
            Bukkit.getLogger().info("[StringModeration] Unregistered from StringsAPI");
        } catch(Exception e) {
            Bukkit.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            Bukkit.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

}
