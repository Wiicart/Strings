package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand implements CommandExecutor {

    private final PlayerDirectMessenger playerDirectMessenger = Strings.getInstance().getPlayerDirectMessenger();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }
        if(!(sender.hasPermission("strings.chat.msg") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.*"))){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length < 1){
            Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }
        StringBuilder message = new StringBuilder();
        for(String arg : args){
            message.append(arg);
            message.append(" ");
        }
        playerDirectMessenger.reply((Player) sender,message.toString().trim());
        return true;
    }
}
