package com.pedestriamc.strings.moderation.commands;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.MessageableSender;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class MuteCommand implements CommandExecutor {

    private final Messenger messenger;

    public MuteCommand(StringsModeration plugin) {
        messenger = StringsProvider.get().getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch(args.length) {
            case 0 -> {
                messenger.sendMessage(Message.INSUFFICIENT_ARGS, new MessageableSender(sender));
                return true;
            }
            case 1 -> {

            }
            case 2 -> {

            }
            default -> {

            }
        }
        return false;
    }
}
