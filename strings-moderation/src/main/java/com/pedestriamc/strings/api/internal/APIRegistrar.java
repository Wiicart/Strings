package com.pedestriamc.strings.api.internal;

import com.pedestriamc.strings.api.StringsModeration;
import com.pedestriamc.strings.api.StringsProvider;
import org.bukkit.Bukkit;

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

    public static void register(com.pedestriamc.strings.api.StringsModeration moderation) {
        try {
            REGISTER.invoke(null, moderation);
            Bukkit.getLogger().info("[StringsModeration] Registered with StringsAPI");
        } catch(Exception e) {
            Bukkit.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            Bukkit.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

    public static void unregister() {
        try {
            UNREGISTER.invoke(null);
            Bukkit.getLogger().info("[StringModeration] Unregistered from StringsAPI");
        } catch(Exception e) {
            Bukkit.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            Bukkit.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

}
