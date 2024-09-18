package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
public class SocialSpyCommand implements CommandExecutor {

    private final Strings strings;

    public SocialSpyCommand(@NotNull Strings strings){
        this.strings = strings;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        Channel socialSpy = strings.getChannel("socialspy");
        if(!(sender.hasPermission("strings.*") || sender.hasPermission("strings.socialspy"))){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length == 0){
            noArgs(sender, socialSpy);
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] This command can only be used by players");
            return true;
        }
        if(args.length > 1){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
            return true;
        }
        switch(args[0]){
            case "on", "true" -> {
                enableSocialSpy(sender, socialSpy);
                return true;
            }
            case "off", "false" -> {
                disableSocialSpy(sender, socialSpy);
                return true;
            }
            default -> {
                noArgs(sender, socialSpy);
                return true;
            }
        }
    }

    public void enableSocialSpy(CommandSender sender, Channel channel){
        strings.getUser((Player) sender).joinChannel(channel);
        Messenger.sendMessage(Message.SOCIAL_SPY_ON, sender);
    }

    public void disableSocialSpy(CommandSender sender, Channel channel){
        strings.getUser((Player) sender).leaveChannel(channel);
        Messenger.sendMessage(Message.SOCIAL_SPY_OFF, sender);
    }

    public void noArgs(CommandSender sender, Channel channel){
        if(strings.getUser((Player) sender).memberOf(channel)){
            disableSocialSpy(sender, channel);
            return;
        }
        enableSocialSpy(sender, channel);
    }
}
