package com.pedestriamc.strings.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Static StringsAPI getter
 */
public final class StringsProvider{

    private static StringsAPI api;

    private StringsProvider(StringsProvider stringsProvider){}

    /**
     * Provides an instance of the StringsAPI.
     * @return A instance of StringsAPI.
     */
    public static StringsAPI get(){
        if(api == null){
            throw new IllegalStateException("Strings API not initialized.");
        }
        return StringsProvider.api;
    }

    /**
     * For internal use only.
     * @param api API Implementation
     * @param plugin Strings plugin
     */
    public static void register(@NotNull StringsAPI api, JavaPlugin plugin){
        if(StringsProvider.api != null){
            throw new IllegalStateException("StringsProvider already initialized.");
        }
        if(!StringsProvider.class.getClassLoader().equals(api.getClass().getClassLoader())){
            throw new SecurityException("Unauthorized attempt to load StringsAPI.");
        }
        StringsProvider.api = api;
        Bukkit.getServer().getServicesManager().register(StringsAPI.class, StringsProvider.api, plugin, ServicePriority.Highest);
        Bukkit.getLogger().info("[Strings] Strings API loaded.");
    }

    /**
     * For internal use only.
     * @param api API Implementation
     * @param plugin Strings plugin
     */
    public static void unregister(){
        StringsProvider.api = null;
        Bukkit.getLogger().info("[Strings] Strings API unloaded.");
    }

    /**
     * Provides a short of the API version.
     * @return A short of the API version.
     */
    public static short getVersion(){
        return 2;
    }
}
