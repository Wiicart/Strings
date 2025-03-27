package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.chat.channel.DefaultChannel;
import com.pedestriamc.strings.commands.CommandBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record ChannelBroadcastCommand(Strings strings) implements CommandBase.CommandComponent {

    // /channel broadcast <channel> <message>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Messenger messenger = strings.getMessenger();
        if(args.length < 3) {
            messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }

        Channel channel = strings.getChannelLoader().getChannel(args[1]);
        if(channel == null) {
            Map<String, String> placeholders = generatePlaceholders(args[1]);
            messenger.sendMessage(Message.CHANNEL_DOES_NOT_EXIST, placeholders, sender);
            return true;
        }

        if(channel instanceof DefaultChannel) {
            messenger.sendMessage(Message.DEFAULT_RESTRICTED, sender);
            return true;
        }

        if(!checkPermissions(sender, channel)) {
            Map<String, String> placeholders = generatePlaceholders(channel.getName());
            messenger.sendMessage(Message.NO_PERMS_CHANNEL, placeholders, sender);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 2; i < args.length; i++) {
            builder.append(args[i]);
        }
        channel.broadcast(builder.toString());

        Map<String, String> placeholders = generatePlaceholders(channel.getName());
        messenger.sendMessage(Message.BROADCAST_SENT, placeholders, sender);

        return true;
    }

    private boolean checkPermissions(CommandSender sender, Channel channel) {
        return
                sender.hasPermission("*") ||
                sender.hasPermission("strings.*") ||
                sender.hasPermission("strings.channels.*") ||
                sender.hasPermission("strings.channels." + channel.getName() + ".*") ||
                sender.hasPermission("strings.channels." + channel.getName() + ".broadcast");
    }

    private Map<String, String> generatePlaceholders(String channelName) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{channel}", channelName);
        return  placeholders;
    }

}
