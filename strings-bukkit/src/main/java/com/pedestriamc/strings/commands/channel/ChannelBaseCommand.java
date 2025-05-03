package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.commands.base.CommandBase;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static com.pedestriamc.strings.api.message.Message.*;

public class ChannelBaseCommand implements CommandBase.CommandComponent {

    private final Messenger messenger;
    private final ChannelLoader channelLoader;
    private final UserUtil userUtil;

    public ChannelBaseCommand(Strings strings) {
        messenger = strings.getMessenger();
        channelLoader = strings.getChannelLoader();
        userUtil = strings.getUserUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch(args.length) {
            case 0 -> messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            case 1 -> oneArg(sender, args);
            case 2 -> twoArgs(sender, args);
            default -> messenger.sendMessage(TOO_MANY_ARGS, sender);
        }
        return true;
    }

    private void oneArg(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("[Strings] Console must define a player to use this command.");
            return;
        }

        Channel channel = channelLoader.getChannel(args[1]);
        if(unsatisfiedConditions(sender, channel, args[1])) {
            return;
        }
        Objects.requireNonNull(channel);

        User user = userUtil.getUser(player.getUniqueId());
        Channel active = user.getActiveChannel();

        if(active != null && active.equals(channel)) {
            messenger.sendMessage(ALREADY_ACTIVE, channelPlaceholders(active.getName()), sender);
            return;
        }

        user.joinChannel(channel);
        user.setActiveChannel(channel);
        save(user);

        messenger.sendMessage(CHANNEL_ACTIVE, channelPlaceholders(channel.getName()), sender);
    }

    private void twoArgs(CommandSender sender, String[] args) {
        if(noPermissionModifyingOther(sender)) {
            messenger.sendMessage(NO_PERMS_MODIFY_OTHER, sender);
        }

        Channel channel = channelLoader.getChannel(args[1]);
        if(unsatisfiedConditions(sender, channel, args[1])) {
            return;
        }
        Objects.requireNonNull(channel);

        User target = getUser(sender, args[2]);
        if(target == null) {
            return;
        }

        if(target.getPlayer().equals(sender)) {
            messenger.sendMessage(CANNOT_USE_ON_SELF, sender);
            return;
        }

        Channel active = target.getActiveChannel();

        if(active != null && active.equals(channel)) {
            messenger.sendMessage(ALREADY_ACTIVE_OTHER, channelPlaceholders(active.getName()), sender);
            return;
        }

        target.joinChannel(channel);
        target.setActiveChannel(channel);
        save(target);

        Map<String, String> placeholders = channelPlaceholders(channel.getName());
        messenger.sendMessage(CHANNEL_ACTIVE, placeholders, target.getPlayer());
        messenger.sendMessage(CHANNEL_ACTIVE_OTHER, placeholders, sender);
    }

    private boolean unsatisfiedConditions(CommandSender sender, Channel channel, String channelName) {
        if(channel == null) {
            messenger.sendMessage(UNKNOWN_CHANNEL, channelPlaceholders(channelName),  sender);
            return true;
        }

        if(channel.getType() == Type.PROTECTED || channel.getMembership() == Membership.PROTECTED) {
            messenger.sendMessage(PROTECTED_CHANNEL_UNSUPPORTED_OPERATION, sender);
            return true;
        }

        if(!channel.allows(sender)) {
            messenger.sendMessage(NO_PERMS_CHANNEL, channelPlaceholders(channelName),  sender);
        }

        return false;
    }

    private User getUser(CommandSender sender, String name) {
        Player p = Bukkit.getPlayer(name);
        if(p == null) {
            messenger.sendMessage(INVALID_PLAYER, Map.of("{player}",name), sender);
            return null;
        }
        return userUtil.getUser(p);
    }

    private boolean noPermissionModifyingOther(@NotNull CommandSender sender) {
        return !sender.isOp() &&
                !sender.hasPermission("*") &&
                !sender.hasPermission("strings.*") &&
                !sender.hasPermission("strings.channels.*") &&
                !sender.hasPermission("strings.channels.modifyplayers");
    }

    private Map<String, String> channelPlaceholders(String name) {
        return Map.of("{channel}", name);
    }

    private void save(User user) {
        userUtil.saveUser(user);
    }

}
