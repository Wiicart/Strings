package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.impl.BukkitMessenger;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.jetbrains.annotations.NotNull;

public class CartMessengerCommand implements CartCommandExecutor {

    private final BukkitMessenger messenger;

    private final Message message;

    public CartMessengerCommand(@NotNull Strings strings, @NotNull Message message) {
        messenger = strings.getMessenger();
        this.message = message;
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        messenger.sendMessage(message, data.sender());
    }
}
