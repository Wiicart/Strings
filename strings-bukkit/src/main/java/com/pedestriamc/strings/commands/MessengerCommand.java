package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MessengerCommand implements CommandExecutor {

    private final Messenger messenger;
    private final Message message;

    @Contract(pure = true)
    public MessengerCommand(@NotNull Strings strings, @NotNull Message message) {
        this.messenger = strings.getMessenger();
        this.message = message;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        messenger.sendMessage(message, sender);
        return true;
    }
}
