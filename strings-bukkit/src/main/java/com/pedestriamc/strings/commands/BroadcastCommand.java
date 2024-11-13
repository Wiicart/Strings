package com.pedestriamc.strings.commands;

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

    private final String broadcastFormat;
    private final boolean usePAPI;
    private final Messenger messenger;


    public BroadcastCommand(@NotNull Strings strings){
        broadcastFormat = strings.getConfig().getString("broadcast-format", "&8[&3Broadcast&8] &f");
        usePAPI = strings.usePlaceholderAPI();
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(!sender.hasPermission("strings.chat.broadcast") && !sender.hasPermission("strings.chat.*") && !sender.hasPermission("strings.*")){
            messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        StringBuilder broadcast = new StringBuilder(broadcastFormat);
        String broadcastString;
        if(sender.hasPermission("strings.chat.placeholdermsg") && usePAPI && (sender instanceof Player)){
            PlaceholderAPI.setPlaceholders((Player) sender, broadcast.toString());
        }
        for(String arg : args){
            broadcast.append(arg);
            broadcast.append(" ");
        }
        broadcastString = broadcast.toString();
        broadcastString = ChatColor.translateAlternateColorCodes('&', broadcastString);
        Bukkit.broadcastMessage(broadcastString);
        return true;
    }
}
