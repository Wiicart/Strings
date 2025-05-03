package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.pedestriamc.strings.api.message.Message.*;
import static com.pedestriamc.strings.api.message.Message.NO_PERMS_CHANNEL;
import static com.pedestriamc.strings.commands.channel.ChannelCommand.CHANNEL_PLACEHOLDER;

@Deprecated
public abstract class ChannelProcessor implements CommandBase.CommandComponent {

    private final ChannelManager channelLoader;
    private final Messenger messenger;

    protected ChannelProcessor(@NotNull Strings strings) {
        this.channelLoader = strings.getChannelLoader();
        this.messenger = strings.getMessenger();
    }

    protected BaseData process(@NotNull CommandSender sender, @NotNull String @NotNull [] args, Type type) {

        Player target;
        boolean modifyingOther = false;
        boolean isPlayer = sender instanceof Player;

        switch(args.length) {
            case 1 -> {
                messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                return new BaseData(null,null, null, true);
            }
            case 2 -> {
                if(!isPlayer) {
                    sender.sendMessage("[Strings] You must define a player to use this command on.");
                    return new BaseData(null,null, null, true);
                } else {
                    target = (Player) sender;
                }
            }
            case 3 -> {
                target = Bukkit.getPlayer(args[2]);
                modifyingOther = true;
                if(target == null) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("{player}", args[2]);
                    messenger.sendMessage(INVALID_PLAYER, map, sender);
                    return new BaseData(null,null, null, true);
                }
            }
            default -> {
                messenger.sendMessage(TOO_MANY_ARGS, sender);
                return new BaseData(null,null, null, true);
            }
        }

        String channelName = args[1];

        if(modifyingOther && !isPlayer && forbids(sender, "strings.channels.modifyplayers")) {
            messenger.sendMessage(NO_PERMS, sender);
            return new BaseData(null,null, null, true);
        }

        if(channelName.equalsIgnoreCase("helpop")) {
            messenger.sendMessage(HELPOP_UNSUPPORTED_OPERATION, sender);
            return new BaseData(null,null, null, true);
        }

        Channel channel = channelLoader.getChannel(channelName);
        if(channel == null) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{channel}", channelName);
            messenger.sendMessage(UNKNOWN_CHANNEL, placeholders,  sender);
            return new BaseData(null,null, null, true);
        }

        if(channelLoader.getProtectedChannels().contains(channel)) {
            messenger.sendMessage(PROTECTED_CHANNEL_UNSUPPORTED_OPERATION, sender);
            return new BaseData(null,null, null, true);
        }

        if((
                type == Type.JOIN ||
                type == Type.BASE) &&
                forbids(sender, "strings.channels." + channel.getName()) &&
                forbids(sender, "strings.channels.*") && forbids(sender, "strings.*") &&
                !channel.getMembers().contains(target)
            ) {
                HashMap<String, String> map = new HashMap<>();
                map.put("{channel}", channelName);
                messenger.sendMessage(NO_PERMS_CHANNEL, map, sender);
                return new BaseData(null, null, null, true);
            }


        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(CHANNEL_PLACEHOLDER, channelName);
        placeholders.put("{player}", target.getName());

        return new BaseData(channel, target, placeholders, false);
    }

    protected enum Type {
        JOIN,
        LEAVE,
        BASE
    }

    protected boolean forbids(CommandSender sender, String perm) {
        return !sender.hasPermission(perm);
    }

}
