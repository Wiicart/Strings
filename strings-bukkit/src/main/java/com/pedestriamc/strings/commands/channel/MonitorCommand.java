package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Monitorable;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.commands.CommandBase;
import com.pedestriamc.strings.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;
import static com.pedestriamc.strings.commands.channel.ChannelCommand.CHANNEL_PLACEHOLDER;

public record MonitorCommand(Strings strings) implements CommandBase.CommandComponent {

    /**
     * /channel monitor <channel> <player>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Messenger messenger = strings.getMessenger();
        if(!sender.hasPermission("strings.channels.monitor")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        Player target;
        if(args.length == 1) {
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        } else if(args.length == 2) {
            if(sender instanceof Player p) {
                target = p;
            } else {
                messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                return true;
            }
        } else if(args.length == 3) {
            Player p = Bukkit.getPlayer(args[2]);
            if(p != null) {
                target = p;
            } else {
                messenger.sendMessage(INVALID_PLAYER, sender);
                return true;
            }
        } else {
            messenger.sendMessage(TOO_MANY_ARGS, sender);
            return true;
        }

        ChannelLoader loader = strings.getChannelLoader();
        Channel channel = loader.getChannel(args[1]);
        if(channel == null) {
            Map<String, String> map = new HashMap<>();
            map.put(CHANNEL_PLACEHOLDER, args[1]);
            messenger.sendMessage(CHANNEL_DOES_NOT_EXIST, map, sender);
            return true;
        }

        if(!sender.hasPermission("strings.channels." + channel.getName() + ".monitor")) {
            Map<String, String> map = generatePlaceholders(sender.getName(), channel.getName());
            messenger.sendMessage(NO_PERM_MONITOR, map, sender);
            return true;
        }

        Monitorable monitorable = Monitorable.of(channel);
        if(monitorable == null) {
            Map<String, String> map = new HashMap<>();
            map.put(CHANNEL_PLACEHOLDER, args[1]);
            messenger.sendMessage(NOT_MONITORABLE, map, sender);
            return true;
        }

        User user = strings.getUser(target);
        if(user.getMonitoredChannels().contains(channel)) {
            messenger.sendMessage(ALREADY_MONITORING, generatePlaceholders(sender.getName(), channel.getName()), sender);
            return true;
        }

        user.monitor(monitorable);

        Map<String, String> placeholders = generatePlaceholders(target.getName(), args[1]);
        messenger.sendMessage(MONITOR_SUCCESS, placeholders, target);
        if(!target.equals(sender)) {
            messenger.sendMessage(MONITOR_SUCCESS_OTHER, placeholders, sender);
        }

        return true;
    }

    private Map<String, String> generatePlaceholders(String playerName, String channelName) {
        Map<String, String> map = new HashMap<>();
        map.put(CHANNEL_PLACEHOLDER, channelName);
        map.put("{player}", playerName);
        return map;
    }

}
