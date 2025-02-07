package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class ReplyCommand implements CommandExecutor {

    private final PlayerDirectMessenger playerDirectMessenger;
    private final Messenger messenger;

    public ReplyCommand(@NotNull Strings strings) {
        this.playerDirectMessenger = strings.getPlayerDirectMessenger();
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(args.length < 1) {
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }

        if(!(sender.hasPermission(
                "strings.chat.msg") ||
                sender.hasPermission("strings.chat.*") ||
                sender.hasPermission("strings.*")
        )) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        StringBuilder message = new StringBuilder();
        for(String arg : args) {
            message.append(arg);
            message.append(" ");
        }

        playerDirectMessenger.reply((Player) sender, message.toString().trim());

        return true;

    }


}
