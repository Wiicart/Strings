package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Bukkit;
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
                if(args[0].equalsIgnoreCase("help")){
                    //help message
                    return true;
                }
                setActiveChannel(sender, args[0]);
                return true;
            }
            case 2 -> {
                /*
                the command has two args, proper usage will be trying to join or leave a channel, or setting the active
                channel of another player.
                 */
                switch(args[1].toUpperCase()){
                    case "JOIN" -> {
                        if(sender instanceof Server){
                            sender.sendMessage("[Strings] /channel join must be used with a player parameter");
                            return true;
                        }
                        //joining channel
                        joinChannel(sender, args[0], (Player) sender);
                        return true;
                    }
                    case "LEAVE" -> {
                        if(sender instanceof Server){
                            sender.sendMessage("[Strings] /channel join must be used with a player parameter");
                            return true;
                        }
                        //leaving channel
                        leaveChannel(sender, args[0], (Player) sender);
                        return true;
                    }
                    default -> {
                        if(Bukkit.getPlayer(args[1]) == null){
                            Player p = Bukkit.getPlayer(args[1]);
                            return true;
                        }
                        if(strings.getCommand(args[0]) != null){
                            Messenger.sendMessage(Message.INVALID_PLAYER, sender);
                            return true;
                        }
                        Messenger.sendMessage(Message.INVALID_USE_CHANNEL, sender);
                        return true;
                    }
                }
            }
            case 3 -> {
                if(args[1].equalsIgnoreCase("join")){
                    if(Bukkit.getPlayer(args[2]) == null){
                        Messenger.sendMessage(Message.INVALID_PLAYER, sender);
                        return true;
                    }
                    joinChannel(sender, args[1], Bukkit.getPlayer(args[2]));
                    return true;
                }
                if(args[1].equalsIgnoreCase("leave")){
                    if(Bukkit.getPlayer(args[2]) == null){
                        Messenger.sendMessage(Message.INVALID_PLAYER, sender);
                        return true;
                    }
                    leaveChannel(sender, args[1], Bukkit.getPlayer(args[2]));
                    return true;
                }
                Messenger.sendMessage(Message.INVALID_USE_CHANNEL, sender);
                return true;
            }
            default -> {
                Messenger.sendMessage(Message.INVALID_USE_CHANNEL, sender);
                return true;
            }
        }
    }
    private void setActiveChannel(CommandSender sender, String channelName){
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

    private void joinChannel(@NotNull CommandSender sender, String channel, Player target){
        boolean modifyingOther = !sender.equals(target);
        //Check if sender has permission to modify other players:
        if(modifyingOther && sender.hasPermission("strings.channels.modifyplayers")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }
        // Check if channel exists:
        Channel c = strings.getChannel(channel);
        if(c == null){
            Messenger.sendMessage(Message.CHANNEL_DOES_NOT_EXIST, sender);
            return;
        }
        if(modifyingOther && !target.hasPermission("strings.channels." + c.getName())){
            Messenger.sendMessage(Message.OTHER_PLAYER_NO_PERMS, sender);
            return;
        }
        User user = strings.getUser(target);
        user.joinChannel(c);
        if(modifyingOther){
            Messenger.sendMessage(Message.OTHER_USER_JOINED_CHANNEL, sender);
            Messenger.sendMessage(Message.CHANNEL_JOINED, target);
            return;
        }
        Messenger.sendMessage(Message.CHANNEL_JOINED, target);
    }

    private void leaveChannel(@NotNull CommandSender sender, String channel, Player target){
        boolean modifyingOther = !sender.equals(target);
        //Check if sender has permission to modify other players:
        if(modifyingOther && sender.hasPermission("strings.channels.modifyplayers")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }
        // Check if channel exists:
        Channel c = strings.getChannel(channel);
        if(c == null){
            Messenger.sendMessage(Message.CHANNEL_DOES_NOT_EXIST, sender);
            return;
        }
        User user = strings.getUser(target);
        user.leaveChannel(c);
        if(modifyingOther){
            Messenger.sendMessage(Message.OTHER_USER_LEFT_CHANNEL, sender);
            Messenger.sendMessage(Message.LEFT_CHANNEL, target);
            return;
        }
        Messenger.sendMessage(Message.LEFT_CHANNEL, target);
    }
}

