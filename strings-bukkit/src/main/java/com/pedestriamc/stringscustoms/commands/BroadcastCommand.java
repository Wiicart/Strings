package com.pedestriamc.stringscustoms.commands;

import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    private final String broadcastFormat = Strings.getInstance().getConfig().getString("broadcast-format");
    private final boolean usePAPI = Strings.getInstance().usePlaceholderAPI();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(!sender.hasPermission("strings.chat.broadcast") && !sender.hasPermission("strings.chat.*") && !sender.hasPermission("strings.*")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        StringBuilder broadcast = new StringBuilder(broadcastFormat);
        String broadCastString;
        if(sender.hasPermission("strings.chat.placeholdermsg") && usePAPI && (sender instanceof Player)){
            PlaceholderAPI.setPlaceholders((Player) sender, broadcast.toString());
        }
        for(String arg : args){
            broadcast.append(arg);
            broadcast.append(" ");
        }
        broadCastString = broadcast.toString();
        broadCastString = ChatColor.translateAlternateColorCodes('&', broadCastString);
        Bukkit.broadcastMessage(broadCastString);
        return true;
    }
}
