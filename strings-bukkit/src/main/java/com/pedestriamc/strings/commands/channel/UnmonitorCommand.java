package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.Monitorable;
import com.pedestriamc.strings.api.message.Message;
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

public record UnmonitorCommand(Strings strings) implements CommandBase.CommandComponent {


    /**
     * /channel unmonitor <channel> <player>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Messenger messenger = strings.getMessenger();
        if(args.length < 2 || (args.length < 3 && !(sender instanceof Player))) {
            messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }

        Channel channel = strings.getChannel(args[1]);
        if(channel == null) {
            messenger.sendMessage(Message.CHANNEL_DOES_NOT_EXIST, sender);
            return true;
        }

        Monitorable monitorable = Monitorable.of(channel);
        if(monitorable == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{name}", channel.getName());
            messenger.sendMessage(Message.NOT_MONITORABLE, placeholders, sender);
        }

        Player target = (Player) sender;
        if(args.length == 3) {
            Player p = Bukkit.getPlayer(args[2]);
            if(p == null) {
                messenger.sendMessage(Message.UNKNOWN_PLAYER, sender);
                return true;
            }
            target = p;
        }

        User user = strings.getUser(target);
        if(!user.getMonitoredChannels().contains(channel)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{name}", channel.getName());
            if(!target.equals(sender)) {
                messenger.sendMessage(Message.NOT_MONITORING_OTHER, placeholders, sender);
            } else {
                messenger.sendMessage(Message.NOT_MONITORING, placeholders, sender);
            }
            return true;
        }

        user.unmonitor(monitorable);

        return true;
    }
}
