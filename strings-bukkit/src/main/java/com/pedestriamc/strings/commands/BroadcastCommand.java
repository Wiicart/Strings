package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.configuration.Option;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.NO_PERMS;

public final class BroadcastCommand implements CommandExecutor {

    private final String broadcastFormat;
    private final boolean usePAPI;
    private final Messenger messenger;


    public BroadcastCommand(@NotNull Strings strings) {
        broadcastFormat = strings.getConfiguration().getString(Option.BROADCAST_FORMAT);
        usePAPI = strings.isUsingPlaceholderAPI();
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!sender.hasPermission("strings.chat.broadcast") && !sender.hasPermission("strings.chat.*") && !sender.hasPermission("strings.*")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        StringBuilder broadcast = new StringBuilder(broadcastFormat);
        String broadcastString;
        if(sender.hasPermission("strings.chat.placeholdermsg") && usePAPI && (sender instanceof Player p)) {
            PlaceholderAPI.setPlaceholders(p, broadcast.toString());
        }
        for(String arg : args) {
            broadcast.append(arg);
            broadcast.append(" ");
        }

        broadcastString = broadcast.toString();
        broadcastString = ChatColor.translateAlternateColorCodes('&', broadcastString);
        Bukkit.broadcastMessage(broadcastString);

        return true;
    }
}
