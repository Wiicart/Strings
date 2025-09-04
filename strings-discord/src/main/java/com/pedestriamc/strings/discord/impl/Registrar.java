package com.pedestriamc.strings.discord.impl;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.discord.StringsDiscord;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

// Based off StringsModeration's APIRegistrar, which is in turn based off LuckPerms
public class Registrar {

    private Registrar() {}

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = StringsProvider.class.getDeclaredMethod("registerDiscord", StringsDiscord.class);
            REGISTER.setAccessible(true);

            UNREGISTER = StringsProvider.class.getDeclaredMethod("unregisterDiscord");
            UNREGISTER.setAccessible(true);
        } catch(NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register(@NotNull StringsDiscordImpl impl, @NotNull com.pedestriamc.strings.discord.StringsDiscord plugin) {
        try {
            REGISTER.invoke(null, impl);
            plugin.getServer()
                    .getServicesManager()
                    .register(StringsDiscord.class, impl, plugin, ServicePriority.Highest);
            plugin.getLogger().info("Registered StringsDiscord with StringsAPI");
        } catch(Exception e) {
            plugin.getLogger().warning("Failed to register StringsDiscord API: " + e.getMessage());
        }
    }

    public static void unregister(@NotNull com.pedestriamc.strings.discord.StringsDiscord plugin) {
        try {
            UNREGISTER.invoke(null);
            plugin.getServer()
                    .getServicesManager()
                    .unregisterAll(plugin);
            plugin.getLogger().info("Unregistered StringsDiscord with StringsAPI");
        } catch(Exception e) {
            plugin.getLogger().warning("Failed to unregister StringsDiscord API: " + e.getMessage());
        }
    }

}
