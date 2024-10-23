package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor
{

    private final Messenger messenger;

    public ClearChatCommand(Strings strings){
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(!sender.hasPermission("strings.chat.clear") && !sender.hasPermission("strings.chat.*") && !sender.hasPermission("strings.*")){
            messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length > 1){
            messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
            return true;
        }
        if(args.length > 0 && args[0].equalsIgnoreCase("all") && (sender.hasPermission("strings.chat.clear.other") || sender.hasPermission("strings.chat.*"))){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.sendMessage(StringUtils.repeat(" \n",100));
                messenger.sendMessage(Message.CHAT_CLEARED, p);
            }
            messenger.sendMessage(Message.CHAT_CLEARED_ALL, sender);
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] Console cannot clear it's own chat.");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(StringUtils.repeat(" \n", 100));
            messenger.sendMessage(Message.CHAT_CLEARED, sender);
            return true;
        }
        messenger.sendMessage(Message.NO_PERMS, sender);
        return true;
    }

}
