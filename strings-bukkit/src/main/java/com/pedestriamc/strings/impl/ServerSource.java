package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.command.Source;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ServerSource implements Source {

    private final CommandSender sender;
    private final Audience console;

    public ServerSource(@NotNull Strings strings) {
        this.sender = strings.getServer().getConsoleSender();
        console = strings.adventure().console();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        console.sendMessage(message);
    }

    @Override
    public @NotNull String getName() {
        return "Server";
    }
}
