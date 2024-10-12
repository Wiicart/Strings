package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.chat.channels.ChannelManager;
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

    private final Strings strings;
    private final ChannelManager channelManager;

    public ChannelCommand(@NotNull Strings strings){
        this.strings = strings;
        this.channelManager = strings.getChannelManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args){
        if(args.length > 3){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
        }
        switch(args.length){
            case 0 -> {
                Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
                return true;
            }
            case 1 -> {
                if(args[0].equalsIgnoreCase("join")){
                    Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
                    return true;
                }
                if(args[0].equalsIgnoreCase("leave")){
                    Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
                    return true;
                }
                if(args[0].equalsIgnoreCase("help")){
                    Messenger.sendMessage(Message.CHANNEL_HELP, sender);
                    return true;
                }
                if(sender instanceof Server){
                    sender.sendMessage("[Strings] This command cannot be used with 1 arg as server.");
                }
                setActiveChannel(sender, args[0], (Player) sender);
                return true;
            }
            case 2 -> {
                /*
                the command has two args, proper usage will be trying to join or leave a channel, or setting the active
                channel of another player.
                 */
                switch(args[0].toUpperCase()){
                    case "JOIN" -> {
                        if(!(sender instanceof Player)){
                            sender.sendMessage("[Strings] /channel join must be used with a player parameter");
                            return true;
                        }
                        //joining channel
                        joinChannel(sender, args[1].toLowerCase(), (Player) sender);
                        return true;
                    }
                    case "LEAVE" -> {
                        if(sender instanceof Server){
                            sender.sendMessage("[Strings] /channel join must be used with a player parameter");
                            return true;
                        }
                        //leaving channel
                        leaveChannel(sender, args[1].toLowerCase(), (Player) sender);
                        return true;
                    }
                    default -> {
                        Player p = Bukkit.getPlayer(args[1]);
                        if(p == null){
                            Messenger.channelCmdMessage(Message.INVALID_PLAYER, sender, args[1], null);
                        }else{
                            setActiveChannel(sender, args[0], p);
                        }
                        return true;
                    }
                }
            }
            case 3 -> {
                if(args[1].equalsIgnoreCase("join")){
                    if(Bukkit.getPlayer(args[2]) == null){
                        Messenger.channelCmdMessage(Message.INVALID_PLAYER, sender, args[2], null);
                        return true;
                    }
                    joinChannel(sender, args[1], Bukkit.getPlayer(args[2]));
                    return true;
                }
                if(args[1].equalsIgnoreCase("leave")){
                    if(Bukkit.getPlayer(args[2]) == null){
                        Messenger.channelCmdMessage(Message.INVALID_PLAYER, sender, args[2], null);
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

    private void setActiveChannel(CommandSender sender, @NotNull String channelName, Player target){
        boolean modifyingOther = !sender.equals(target);
        if(modifyingOther && !sender.hasPermission("strings.channel.modifyplayers") && sender instanceof Player){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }
        Channel c = strings.getChannel(channelName);
        if(c == null){
            Messenger.channelCmdMessage(Message.CHANNEL_DOES_NOT_EXIST, sender, target.getName(), channelName);
            return;
        }
        if(channelManager.getProtectedChannels().contains(c)){
            Messenger.sendMessage(Message.PROTECTED_CHANNEL, sender);
            return;
        }
        if (!target.hasPermission("strings.channels." + c.getName()) &&
                !target.hasPermission("strings.channels.*") &&
                !target.hasPermission("strings.*") &&
                !target.hasPermission("strings.channels.channel.*")) {
            if (modifyingOther) {
                Messenger.channelCmdMessage(Message.OTHER_PLAYER_NO_PERMS, sender, target.getName(), channelName);
            } else {
                Messenger.channelCmdMessage(Message.NO_PERMS_CHANNEL, sender, sender.getName(), channelName);
            }
            return;
        }
        User user = strings.getUser(target);
        user.setActiveChannel(c);
        if(modifyingOther){
            Messenger.channelCmdMessage(Message.OTHER_PLAYER_CHANNEL_ACTIVE, sender, target.getName(), channelName);
        }
        Messenger.channelCmdMessage(Message.CHANNEL_ACTIVE, target, target.getName(), channelName);

    }

    private void joinChannel(@NotNull CommandSender sender, String channel, Player target){
        boolean modifyingOther = !sender.equals(target);
        //Check if sender has permission to modify other players:
        if(modifyingOther && !sender.hasPermission("strings.channels.modifyplayers") && sender instanceof Player){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }
        // Check if channel exists:
        Channel c = strings.getChannel(channel);
        if(c == null){
            Messenger.channelCmdMessage(Message.CHANNEL_DOES_NOT_EXIST, sender, target.getName(), channel);
            return;
        }
        if(c.getName().equals("helpop")){
            Messenger.sendMessage(Message.HELPOP_NOT_CHANNEL, sender);
            return;
        }
        if(channelManager.getProtectedChannels().contains(c)){
            Messenger.sendMessage(Message.PROTECTED_CHANNEL, sender);
            return;
        }
        if (!target.hasPermission("strings.channels." + c.getName()) &&
                !target.hasPermission("strings.channels.*") &&
                !target.hasPermission("strings.*")) {
            if (modifyingOther) {
                Messenger.channelCmdMessage(Message.OTHER_PLAYER_NO_PERMS, sender, target.getName(), channel);
            } else {
                Messenger.channelCmdMessage(Message.NO_PERMS_CHANNEL, sender, sender.getName(), channel);
            }
            return;
        }
        User user = strings.getUser(target);
        user.joinChannel(c);
        if(modifyingOther){
            Messenger.channelCmdMessage(Message.OTHER_USER_JOINED_CHANNEL, sender, target.getName(), channel);
        }
        Messenger.channelCmdMessage(Message.CHANNEL_JOINED, target, target.getName(), channel);
    }

    private void leaveChannel(@NotNull CommandSender sender, String channel, Player target){
        boolean modifyingOther = !sender.equals(target);
        //Check if sender has permission to modify other players:
        if(modifyingOther && !sender.hasPermission("strings.channels.modifyplayers")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }
        // Check if channel exists:
        Channel c = strings.getChannel(channel);
        if(c == null){
            Messenger.channelCmdMessage(Message.CHANNEL_DOES_NOT_EXIST, sender, target.getName(), channel);
            return;
        }
        if(channelManager.getProtectedChannels().contains(c)){
            Messenger.sendMessage(Message.PROTECTED_CHANNEL, sender);
            return;
        }
        if(c.getName().equals("global")){
            Messenger.sendMessage(Message.CANT_LEAVE_GLOBAL, sender);
            return;
        }
        User user = strings.getUser(target);
        if(!user.getChannels().contains(c)){
            if(modifyingOther){
                Messenger.channelCmdMessage(Message.NOT_CHANNEL_MEMBER_OTHER, sender, target.getName(), channel);
            }
            Messenger.channelCmdMessage(Message.NOT_CHANNEL_MEMBER, target, target.getName(), channel);
            return;
        }
        user.leaveChannel(c);
        if(modifyingOther){
            Messenger.channelCmdMessage(Message.OTHER_USER_LEFT_CHANNEL, sender, target.getName(), channel);
        }
        Messenger.channelCmdMessage(Message.LEFT_CHANNEL, target, target.getName(), channel);
    }
}
