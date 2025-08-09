package com.pedestriamc.strings.impl;

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
}
