package com.pedestriamc.strings.api.internal;

import com.pedestriamc.strings.api.StringsModeration;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.UUID;

// Reflection-based registrar, based off LuckPerms implementation.
public final class APIRegistrar {

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = StringsModerationRegistrar.class.getDeclaredMethod("register", StringsModeration.class, UUID.class);
            REGISTER.setAccessible(true);

            UNREGISTER = StringsModerationRegistrar.class.getDeclaredMethod("unregister", UUID.class);
            UNREGISTER.setAccessible(true);
        } catch(NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private APIRegistrar() {}

    public static void register(com.pedestriamc.strings.api.StringsModeration moderation, UUID uuid) {
        try {
            REGISTER.invoke(null, moderation, uuid);
        } catch(Exception e) {
            Bukkit.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            Bukkit.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

    public static void unregister(UUID uuid) {
        try {
            UNREGISTER.invoke(null, uuid);
        } catch(Exception e) {
            Bukkit.getLogger().warning("[StringsModeration] Failed to register StringsModeration API");
            Bukkit.getLogger().warning("[StringsModeration] Error: " + e.getMessage());
        }
    }

}
