package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelCommand implements CommandExecutor {

    private Strings strings = Strings.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(args.length > 3){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
        }
        switch(args.length){
            case 0 -> {
                Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
                return true;
            }
            case 1 -> {
                setActiveChannel(sender, args[0]);
                return true;
            }
            case 2 -> {
                return true;
            }
            default -> {
                return true;
            }
        }
    }
    public void setActiveChannel(CommandSender sender, String channelName){
        if(sender instanceof Server){
            sender.sendMessage("[Strings] This command cannot be used with 1 arg as server.");
            return;
        }
        if(sender.hasPermission("strings.channel." + channelName) || sender.hasPermission("strings.channel.*") || sender.hasPermission("strings.*")){
            Channel channel = strings.getChannel(channelName);
            if(channel != null){
                strings.getUser((Player) sender).setActiveChannel(channel);
                Messenger.sendMessage(Message.CHANNEL_ACTIVE, sender);
            }
            return;
        }
        Messenger.sendMessage(Message.NO_PERMS, sender);
    }

}
