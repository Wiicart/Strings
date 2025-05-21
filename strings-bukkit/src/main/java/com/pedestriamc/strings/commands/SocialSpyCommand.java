package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class SocialSpyCommand implements CommandExecutor {

    private final Messenger messenger;
    private final UserUtil userUtil;
    private final Channel channel;

    public SocialSpyCommand(@NotNull Strings strings) {
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
        channel = strings.getChannelLoader().getChannel("socialspy");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("[Strings] This command can only be used by players");
            return true;
        }

        if(!Permissions.anyOfOrAdmin(sender, "strings.*", "strings.socialspy")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        if(args.length == 0) {
            noArgs(sender);
            return true;
        } else if(args.length > 1) {
            messenger.sendMessage(TOO_MANY_ARGS, sender);
            return true;
        }

        determine(args[0], sender);
        return true;
    }

    private void determine(@NotNull String arg, @NotNull CommandSender sender) {
        switch(arg.toLowerCase()) {
            case "on", "true" -> enableSocialSpy(sender);

            case "off", "false" -> disableSocialSpy(sender);

            default -> noArgs(sender);
        }
    }

    private void enableSocialSpy(CommandSender sender) {
        User user = userUtil.getUser((Player) sender);
        user.joinChannel(channel);
        userUtil.saveUser(user);
        messenger.sendMessage(SOCIAL_SPY_ON, sender);
    }

    private void disableSocialSpy(CommandSender sender) {
        User user = userUtil.getUser((Player) sender);
        user.leaveChannel(channel);
        userUtil.saveUser(user);
        messenger.sendMessage(SOCIAL_SPY_OFF, sender);
    }

    private void noArgs(CommandSender sender) {
        User user = userUtil.getUser((Player) sender);
        if(user.memberOf(channel)) {
            disableSocialSpy(sender);
            return;
        }
        enableSocialSpy(sender);
    }
}
