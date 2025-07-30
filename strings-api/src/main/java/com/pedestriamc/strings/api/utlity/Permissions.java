package com.pedestriamc.strings.api.utlity;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class used to simplify checking for multiple permissions, and to simplify permission registration.
 */
@Deprecated()
public final class Permissions {

    private final PluginManager pluginManager;

    /**
     * The sole constructor for this class.
     * @param plugin A plugin instance on the server.
     */
    public Permissions(@NotNull JavaPlugin plugin) {
        Objects.requireNonNull(plugin);
        pluginManager = plugin.getServer().getPluginManager();
    }

    /**
     * Registers a Permission with the PluginManager.
     * @param permission The permission to register.
     */
    public void addPermission(@NotNull Permission permission) {
        pluginManager.addPermission(permission);
    }

    /**
     * Registers Permissions with the PluginManager.
     * @param permissions The Permissions to be registered.
     */
    public void addPermissions(@NotNull Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            addPermission(permission);
        }
    }

    /**
     * Registers a Permission with the PluginManager.
     * @param permission The permission to register.
     */
    public void addPermission(@NotNull String permission) {
        addPermission(new Permission(permission));
    }

    /**
     * Registers Permissions with the PluginManager.
     * @param permissions The Permissions to be registered.
     */
    public void addPermissions(String @NotNull ... permissions) {
        for (String permission : permissions) {
            addPermission(permission);
        }
    }

    /**
     * Unregisters a Permissions with the PluginManager.
     * @param permission The Permission to be unregistered.
     */
    public void removePermission(@NotNull Permission permission) {
        pluginManager.removePermission(permission);
    }

    /**
     * Unregisters Permissions with the PluginManager.
     * @param permissions The Permissions to be unregistered.
     */
    public void removePermissions(Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            removePermission(permission);
        }
    }

    /**
     * Unregisters a Permissions with the PluginManager.
     * @param permission The Permission to be unregistered.
     */
    public void removePermission(@NotNull String permission) {
        pluginManager.removePermission(permission);
    }

    /**
     * Unregisters Permissions with the PluginManager.
     * @param permissions The Permissions to be unregistered.
     */
    public void removePermissions(String @NotNull ... permissions) {
        for (String permission : permissions) {
            removePermission(permission);
        }
    }

    /**
     * Returns if any of the permissions provided are true for the permissible.
     * @param permissible The permissible to check permissions of.
     * @param permissions The permissions that are being checked for.
     * @return True if any of the permissions are true.
     */
    public static boolean anyOf(@NotNull Permissible permissible, String @NotNull ... permissions) {
        for (String permission : permissions) {
            if (permissible.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if any of the permissions provided are true for the permissible.
     * @param permissible The permissible to check permissions of.
     * @param permissions The permissions that are being checked for.
     * @return True if any of the permissions are true.
     */
    public static boolean anyOf(@NotNull Permissible permissible, Permission @NotNull ... permissions) {
        for (Permission permission : permissions) {
            if (permissible.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if any of the permissions provided are true for the permissible, or if the permissible is OP or has permission *
     * @param permissible The permissible to check permissions of.
     * @param permissions The permissions that are being checked for.
     * @return True if any of the permissions are true.
     */
    public static boolean anyOfOrAdmin(@NotNull Permissible permissible, @NotNull String... permissions) {
        return permissible.isOp() || permissible.hasPermission("*") || anyOf(permissible, permissions);
    }

    /**
     * Returns if any of the permissions provided are true for the permissible, or if the permissible is OP or has permission *
     * @param permissible The permissible to check permissions of.
     * @param permissions The permissions that are being checked for.
     * @return True if any of the permissions are true.
     */
    public static boolean anyOfOrAdmin(@NotNull Permissible permissible, @NotNull Permission... permissions) {
        return permissible.isOp() || permissible.hasPermission("*") || anyOf(permissible, permissions);
    }

    /**
     * Checks if a Permissible has all the listed defined permissions.
     * @param permissible The Permissible to check the permissions of.
     * @param permissions The permission to check for.
     * @return If the Permissible has permission.
     */
    public static boolean allOf(@NotNull Permissible permissible, String @NotNull ... permissions) {
        for(String permission : permissions) {
            if (!permissible.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a Permissible has all the listed defined permissions.
     * @param permissible The Permissible to check the permissions of.
     * @param permissions The permission to check for.
     * @return If the Permissible has permission.
     */
    public static boolean allOf(@NotNull Permissible permissible, Permission @NotNull ... permissions) {
        for(Permission permission : permissions) {
            if (!permissible.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
}
