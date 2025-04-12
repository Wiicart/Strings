package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class SocialSpyCommand implements CommandExecutor {

    private final Strings strings;
    private final Messenger messenger;
    private final UserUtil userUtil;

    public SocialSpyCommand(@NotNull Strings strings) {
        this.strings = strings;
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //TODO Review if social spy channel can be instance method
        Channel socialSpy = strings.getChannelLoader().getChannel("socialspy");
        if(!(sender.hasPermission("strings.*") || sender.hasPermission("strings.socialspy"))) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        if(args.length == 0) {
            noArgs(sender, socialSpy);
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage("[Strings] This command can only be used by players");
            return true;
        }

        if(args.length > 1) {
            messenger.sendMessage(TOO_MANY_ARGS, sender);
            return true;
        }
        switch(args[0]) {
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

    public void enableSocialSpy(CommandSender sender, Channel channel) {
        User user = userUtil.getUser((Player) sender);
        user.joinChannel(channel);
        userUtil.saveUser(user);
        messenger.sendMessage(SOCIAL_SPY_ON, sender);
    }

    public void disableSocialSpy(CommandSender sender, Channel channel) {
        User user = userUtil.getUser((Player) sender);
        user.leaveChannel(channel);
        userUtil.saveUser(user);
        messenger.sendMessage(SOCIAL_SPY_OFF, sender);
    }

    public void noArgs(CommandSender sender, Channel channel) {
        User user = userUtil.getUser((Player) sender);
        if(user.memberOf(channel)) {
            disableSocialSpy(sender, channel);
            return;
        }
        enableSocialSpy(sender, channel);
    }
}
