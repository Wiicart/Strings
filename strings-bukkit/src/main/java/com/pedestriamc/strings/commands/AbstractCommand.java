package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.impl.BukkitMessenger;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractCommand {

    private final Strings strings;

    protected AbstractCommand(@NotNull Strings strings) {
        this.strings = strings;
    }

    protected Strings strings() {
        return strings;
    }

    protected UserUtil users() {
        return strings.users();
    }

    protected BukkitMessenger messenger() {
        return strings.getMessenger();
    }

    protected void sendMessage(@NotNull Message message, @NotNull CommandSender sender) {
        messenger().sendMessage(message, sender);
    }

    protected void sendMessage(@NotNull Message message, @NotNull Map<String, String> placeholders, @NotNull CommandSender recipient) {
        messenger().sendMessage(message, placeholders, recipient);
    }

    protected void saveUser(@NotNull User user) {
        users().saveUser(user);
    }

    protected User getUser(@NotNull Player player) {
        return users().getUser(player);
    }

    protected User getUser(@NotNull String name) {
        return users().getUser(name);
    }

    protected User getUser(@NotNull UUID uuid) {
        return users().getUser(uuid);
    }

    protected Channel getChannel(@NotNull String name) {
        return strings.getChannelLoader().getChannel(name);
    }

}
