package com.pedestriamc.strings.bukkit;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MessageableAdapter {

    @Contract("_ -> new")
    public static @NotNull Messageable of(@NotNull CommandSender sender) {
        return new CommandSenderMessageable(sender);
    }

    public static @NotNull Messageable of(@NotNull CommandSender sender, @NotNull Strings strings) {
        return new ScheduledCommandSenderMessageable(sender, strings);
    }

    private record CommandSenderMessageable(@NotNull CommandSender sender) implements Messageable {

        @Override
        public void sendMessage(@NotNull String message) {
            sender.sendMessage(message);
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            sendMessage(ComponentConverter.toString(message));
        }

    }

    private record ScheduledCommandSenderMessageable(@NotNull CommandSender sender,
                                                     @NotNull Strings strings) implements Messageable {

        @Override
        public void sendMessage(@NotNull String message) {
            if (sender instanceof org.bukkit.entity.Player player) {
                strings.forEntity(strings, player, () -> player.sendMessage(message));
            } else {
                sender.sendMessage(message);
            }
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            sendMessage(ComponentConverter.toString(message));
        }
    }
}
