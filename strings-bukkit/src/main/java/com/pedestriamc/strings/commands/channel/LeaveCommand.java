package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.commands.base.CommandBase;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;

public class LeaveCommand implements CommandBase.CommandComponent {

    private final Strings strings;
    private final ChannelLoader channelLoader;
    private final Messenger messenger;
    private final UserUtil userUtil;

    public LeaveCommand(@NotNull Strings strings) {
        this.strings = strings;
        channelLoader = strings.getChannelLoader();
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Channel channel;
        Player target;
        switch(args.length) {
            case 0, 1 -> {
                messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                return true;
            }

            // /channel leave <channel>
            case 2 -> {
                if(sender instanceof Player p) {
                    target = p;
                } else {
                    sender.sendMessage("[Strings] You must define a player to use this command on.");
                    return true;
                }

                channel = channelLoader.getChannel(args[1]);
                if(shouldRejectChannel(sender, target, channel, args[1])) {
                    return true;
                }
            }

            // /channel leave <channel> <player>
            case 3 -> {
                if(!sender.hasPermission("strings.channels.modifyplayers")){
                    messenger.sendMessage(NO_PERMS, sender);
                    return true;
                }

                target = strings.getServer().getPlayer(args[3]);
                if(target == null) {
                    messenger.sendMessage(UNKNOWN_PLAYER, sender);
                    return true;
                }

                channel = channelLoader.getChannel(args[1]);
                if(shouldRejectChannel(sender, target, channel, args[1])) {
                    return true;
                }
            }

            default -> {
                messenger.sendMessage(TOO_MANY_ARGS, sender);
                return true;
            }
        }

        leave(sender, channel, target);

        return true;
    }

    /**
     * Has the player join the Channel if conditions are met
     * @param sender The CommandSender
     * @param channel The Channel used
     * @param target The Player that would join the channel
     */
    private void leave(CommandSender sender, Channel channel, Player target) {
        boolean modifyingOther = !sender.equals(target);
        User user = userUtil.getUser(target);
        Map<String, String> placeholders = getPlaceholders(target, channel);

        user.leaveChannel(channel);
        userUtil.saveUser(user);

        if(modifyingOther) {
            messenger.sendMessage(LEFT_CHANNEL_OTHER, placeholders, sender);
        }
        messenger.sendMessage(LEFT_CHANNEL, placeholders, target);
    }

    private Map<String, String> getPlaceholders(@NotNull CommandSender target, @NotNull Channel channel) {
        return Map.of(
                "{channel}", channel.getName(),
                "{player}", target.getName()
        );
    }

    /**
     * Checks channel permissions
     * @param sender The CommandSender
     * @param channel The Channel to check against
     * @return False if the command should return, true if the command should not.
     */
    private boolean shouldRejectChannel(CommandSender sender, Player target, Channel channel, String channelName) {
        if(channel == null) {
            messenger.sendMessage(UNKNOWN_CHANNEL, Map.of("{channel}", channelName), sender);
            return true;
        }

        User user = userUtil.getUser(target);
        if(!user.memberOf(channel)) {
            if(sender.equals(target)) {
                messenger.sendMessage(NOT_CHANNEL_MEMBER, getPlaceholders(target, channel), sender);
            } else {
                messenger.sendMessage(NOT_CHANNEL_MEMBER_OTHER, getPlaceholders(target, channel), sender);
            }
            return true;
        }

        if(channel.getType() == Type.PROTECTED && !(channel instanceof HelpOPChannel)) {
            messenger.sendMessage(PROTECTED_CHANNEL_UNSUPPORTED_OPERATION, sender);
            return true;
        }

        if(channel.getType() == Type.DEFAULT) {
            messenger.sendMessage(CANT_LEAVE_DEFAULT, sender);
            return true;
        }

        return false;
    }

}
