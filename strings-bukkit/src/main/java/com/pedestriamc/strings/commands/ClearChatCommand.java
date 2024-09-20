package com.pedestriamc.strings.commands;

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
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //By default, the command clear chat will only clear the sender's chat.  If args[0] is equal to "all", given the user
        //has the permission strings.chat.clear.all, it will clear all player's chat.
        //Some code from this thread https://www.spigotmc.org/threads/best-way-to-clear-chat.323466/

        //Check if player has the most basic permissions
        if(!sender.hasPermission("strings.chat.clear") && !sender.hasPermission("strings.chat.*") && !sender.hasPermission("strings.*")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length > 1){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
            return true;
        }
        // Clear chat for all
        if(args.length > 0 && args[0].equalsIgnoreCase("all") && (sender.hasPermission("strings.chat.clear.other") || sender.hasPermission("strings.chat.*"))){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.sendMessage(StringUtils.repeat(" \n",100));
                Messenger.sendMessage(Message.CHAT_CLEARED, p);
            }
            Messenger.sendMessage(Message.CHAT_CLEARED_ALL, sender);
            return true;
        }
        //Self chat clear
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] Console cannot clear it's own chat.");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(StringUtils.repeat(" \n", 100));
            Messenger.sendMessage(Message.CHAT_CLEARED, sender);
            return true;
        }
        Messenger.sendMessage(Message.NO_PERMS, sender);
        return true;
    }
}
