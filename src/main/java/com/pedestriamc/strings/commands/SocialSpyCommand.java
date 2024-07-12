package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.SocialSpy;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SocialSpyCommand implements CommandExecutor {

    private final Strings strings = Strings.getInstance();
    private final SocialSpy socialSpy = strings.getSocialSpy();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(!(sender.hasPermission("strings.*") || sender.hasPermission("strings.socialspy"))){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length == 0){
            noArgs(sender);
            return true;
        }
        if(sender instanceof Server){
            sender.sendMessage("[Strings] This command can only be used by players");
            return true;
        }
        if(args.length > 1){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
            return true;
        }
        switch(args[0]){
            case "on", "true" -> {
                enableSocialSpy(sender);
                return true;
            }
            case "off", "false" -> {
                disableSocialSpy(sender);
                return true;
            }
            default -> {
                noArgs(sender);
                return true;
            }
        }
    }

    public void enableSocialSpy(CommandSender sender){
        strings.getUser((Player) sender).setSocialSpy(true);
        socialSpy.addSpy(sender);
        Messenger.sendMessage(Message.SOCIAL_SPY_ON, sender);
    }

    public void disableSocialSpy(CommandSender sender){
        strings.getUser((Player) sender).setSocialSpy(false);
        socialSpy.removeSpy(sender);
        Messenger.sendMessage(Message.SOCIAL_SPY_OFF, sender);
    }
    public void noArgs(CommandSender sender){
        if(strings.getUser((Player) sender).isSocialSpy()){
            disableSocialSpy(sender);
            return;
        }
        enableSocialSpy(sender);
    }
}
