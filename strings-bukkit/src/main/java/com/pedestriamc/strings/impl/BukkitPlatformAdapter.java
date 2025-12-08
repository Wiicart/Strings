package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.MessageUtilities;
import com.pedestriamc.strings.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public class BukkitPlatformAdapter implements PlatformAdapter {

    private final Strings strings;

    public BukkitPlatformAdapter(@NotNull Strings strings) {
        this.strings = strings;
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<StringsUser> getOnlineUsers() {
        return strings.users().getUsers();
    }

    @Override
    public String colorHex(@NotNull String input) {
        return MessageUtilities.colorHex(input);
    }

    @Override
    public String translateBukkitColor(@NotNull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    @Override
    public String stripBukkitColor(@NotNull String input) {
        return ChatColor.stripColor(input);
    }

    @Override
    public String applyPlaceholders(@NotNull StringsUser source, @NotNull String input) {
        try {
            return PlaceholderAPI.setPlaceholders(User.playerOf(source), input);
        } catch (NoClassDefFoundError e) {
            return input;
        }
    }

    @Override
    public String processMentions(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String str) {
        return strings.getMentioner().processMentions(User.playerOf(sender), channel, str);
    }

    @Override
    public void removePermission(@NotNull String... permission) {
        for (String perm : permission) {
            try {
                strings.getServer().getPluginManager().removePermission(perm);
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void addPermission(@NotNull String... permission) {
        for (String perm : permission) {
            try {
                strings.getServer().getPluginManager().addPermission(new Permission(perm));
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void print(@NotNull String message) {
        Bukkit.getLogger().info(message);
    }

}
