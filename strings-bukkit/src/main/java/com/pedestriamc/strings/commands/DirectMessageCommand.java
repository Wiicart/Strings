package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DirectMessageCommand implements CommandExecutor {

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
        if(args.length < 2){
            Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }
        Player recipient = Bukkit.getPlayer(args[0]);
        if(recipient == null){
            Messenger.sendMessage(Message.UNKNOWN_PLAYER, sender);
            return true;
        }
        if(recipient.equals((Player) sender)){
            Messenger.sendMessage(Message.SELF_MESSAGE, sender);
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }

        playerDirectMessenger.sendMessage((Player) sender, recipient, stringBuilder.toString());

        return true;
    }
}
