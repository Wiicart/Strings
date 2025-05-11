package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.commands.base.CommandBase;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UnmonitorCommand implements CommandBase.CommandComponent {

    private final Strings strings;

    public UnmonitorCommand(Strings strings) {
        this.strings = strings;
    }

    /**
     * /channel unmonitor <channel> <player>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Messenger messenger = strings.getMessenger();
        UserUtil userUtil = strings.getUserUtil();
        if(args.length < 2 || (args.length < 3 && !(sender instanceof Player))) {
            messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }

        Channel channel = strings.getChannelLoader().getChannel(args[1]);
        if(channel == null) {
            messenger.sendMessage(Message.UNKNOWN_CHANNEL, Map.of("{channel}", args[1]), sender);
            return true;
        }

        Monitorable monitorable = Monitorable.of(channel);
        if(monitorable == null) {
            Map<String, String> placeholders = generatePlaceholders(channel.getName(), "");
            messenger.sendMessage(Message.NOT_MONITORABLE, placeholders, sender);
            return true;
        }

        Player target;
        if(args.length == 3) {
            Player p = Bukkit.getPlayer(args[2]);
            if(p == null) {
                messenger.sendMessage(Message.UNKNOWN_PLAYER, sender);
                return true;
            }
            target = p;
        } else if(sender instanceof Player p) {
             target = p;
        } else {
            return true;
        }

        User user = strings.getUserUtil().getUser(target);
        if(!user.getMonitoredChannels().contains(channel)) {
            Map<String, String> placeholders = generatePlaceholders(channel.getName(), target.getName());
            if(!target.equals(sender)) {
                messenger.sendMessage(Message.NOT_MONITORING_OTHER, placeholders, sender);
            } else {
                messenger.sendMessage(Message.NOT_MONITORING, placeholders, sender);
            }
            return true;
        }

        user.unmonitor(monitorable);
        userUtil.saveUser(user);

        Map<String, String> placeholders = generatePlaceholders(channel.getName(), target.getName());
        if(!target.equals(sender)) {
            messenger.sendMessage(Message.UNMONITORED_OTHER, placeholders, sender);
        }
        messenger.sendMessage(Message.UNMONITORED, placeholders, sender);

        return true;
    }

    private Map<String, String> generatePlaceholders(String channel, String player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{channel}", channel);
        placeholders.put("{player}", player);
        return placeholders;
    }
}
