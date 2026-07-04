package com.pedestriamc.strings.integration.placeholderapi;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for {@code PlaceholderAPI.setPlaceholders()}.<br/>
 * Returns the original String if PlaceholderAPI is unavailable.
 */
public class PlaceholderAPISetter {

    private final boolean enabled;
    
    public PlaceholderAPISetter(@NotNull Strings strings) {
        boolean classFound = false;
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            classFound = true;
        } catch(ClassNotFoundException ignored) {}
        this.enabled = classFound;
    }

    public String setPlaceholders(@NotNull Player player, @NotNull String message) {
        if (enabled) {
            try {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
            } catch(Exception ignored) {}
        }
        return message;
    }

    public String setPlaceholders(@NotNull StringsUser user, @NotNull String message) {
        return setPlaceholders(User.playerOf(user), message);
    }

    public Component setPlaceholders(@NotNull Player player, @NotNull Component message) {
        if (enabled) {
            try {
                return me.clip.placeholderapi.PAPIComponents.setPlaceholders(player, message);
            } catch(Exception ignored) {}
        }
        return message;
    }

    public Component setPlaceholders(@NotNull StringsUser user, @NotNull Component message) {
        return setPlaceholders(User.playerOf(user), message);
    }

}
