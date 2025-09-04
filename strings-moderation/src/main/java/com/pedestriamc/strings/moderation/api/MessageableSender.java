package com.pedestriamc.strings.moderation.api;

import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record MessageableSender(@NotNull CommandSender sender) implements Messageable {

    @Override
    public void sendMessage(@NotNull String message) {
        sender.sendMessage(message);
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        sendMessage(ComponentConverter.toString(message));
    }

}
