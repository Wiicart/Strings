package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class SocialSpyCommand implements CommandExecutor {

    private final Strings strings;
    private final Messenger messenger;

    public SocialSpyCommand(@NotNull Strings strings){
        this.strings = strings;
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        Channel socialSpy = strings.getChannel("socialspy");
        if(!(sender.hasPermission("strings.*") || sender.hasPermission("strings.socialspy"))){
            messenger.sendMessage(NO_PERMS, sender);
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
            messenger.sendMessage(TOO_MANY_ARGS, sender);
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
        messenger.sendMessage(SOCIAL_SPY_ON, sender);
    }

    public void disableSocialSpy(CommandSender sender, Channel channel){
        strings.getUser((Player) sender).leaveChannel(channel);
        messenger.sendMessage(SOCIAL_SPY_OFF, sender);
    }

    public void noArgs(CommandSender sender, Channel channel){
        if(strings.getUser((Player) sender).memberOf(channel)){
            disableSocialSpy(sender, channel);
            return;
        }
        enableSocialSpy(sender, channel);
    }
}
