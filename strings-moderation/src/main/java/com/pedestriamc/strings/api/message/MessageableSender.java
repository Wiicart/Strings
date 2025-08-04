package com.pedestriamc.strings.api.message;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record MessageableSender(@NotNull CommandSender sender) implements Messageable {

    @Override
    public void sendMessage(@NotNull String message) {
        sender.sendMessage(message);
    }

}
