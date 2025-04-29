package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.commands.CommandBase;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;
import static com.pedestriamc.strings.api.message.Message.CHANNEL_JOINED;

/**
 * Channel join sub command
 */
public class JoinCommand implements CommandBase.CommandComponent {

    private final Strings strings;
    private final Messenger messenger;
    private final UserUtil userUtil;
    private final ChannelLoader channelLoader;

    public JoinCommand(Strings strings) {
        this.strings = strings;
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
        channelLoader = strings.getChannelLoader();
    }

    //Syntax: /channel join <channel> <user>
    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Channel channel;
        Player target;
        switch(args.length) {
            case 0, 1, 2 -> {
                messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                return true;
            }

            // /channel join <channel>
            case 3 -> {
                if(sender instanceof Player p) {
                    target = p;
                } else {
                    sender.sendMessage("[Strings] You must define a player to use this command on.");
                    return true;
                }

                channel = channelLoader.getChannel(args[2]);
                if(shouldRejectChannel(sender, channel)) {
                    return true;
                }
            }

            // /channel join <channel> <player>
            case 4 -> {
                if(!sender.hasPermission("strings.channels.modifyplayers")){
                    messenger.sendMessage(NO_PERMS, sender);
                    return true;
                }

                target = strings.getServer().getPlayer(args[3]);
                if(target == null) {
                    messenger.sendMessage(UNKNOWN_PLAYER, sender);
                    return true;
                }

                channel = channelLoader.getChannel(args[2]);
                if(shouldRejectChannel(sender, channel)) {
                    return true;
                }
            }

            default -> {
                messenger.sendMessage(TOO_MANY_ARGS, sender);
                return true;
            }
        }

        joinIfEligible(sender, channel, target);

        return true;
    }

    /**
     * Has the player join the Channel if conditions are met
     * @param sender The CommandSender
     * @param channel The Channel used
     * @param target The Player that would join the channel
     */
    private void joinIfEligible(CommandSender sender, Channel channel, Player target) {
        boolean modifyingOther = !sender.equals(target);
        User user = userUtil.getUser(target);
        Map<String, String> placeholders = getPlaceholders(target, channel);

        if(user.memberOf(channel)) {
            if(modifyingOther) {
                messenger.sendMessage(ALREADY_MEMBER_OTHER, placeholders, sender);
            } else {
                messenger.sendMessage(ALREADY_MEMBER, placeholders, sender);
            }
            return;
        }

        user.joinChannel(channel);
        userUtil.saveUser(user);

        if(modifyingOther) {
            messenger.sendMessage(OTHER_USER_JOINED_CHANNEL, placeholders, sender);
        }
        messenger.sendMessage(CHANNEL_JOINED, placeholders, target);
    }

    private Map<String, String> getPlaceholders(@NotNull Player target, @NotNull Channel channel) {
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
    private boolean shouldRejectChannel(CommandSender sender, Channel channel) {
        if(channel == null) {
            messenger.sendMessage(UNKNOWN_CHANNEL, sender);
            return true;
        }

        if(!channel.allows(sender)) {
            Map<String, String> placeholders = Map.of("{channel}", channel.getName());
            messenger.sendMessage(NO_PERMS_CHANNEL, placeholders, sender);
            return true;
        }

        return false;
    }

}