package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

class BroadcastCommand extends AbstractChannelCommand implements CartCommandExecutor {

    BroadcastCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        String[] args = data.args();
        CommandSender sender = data.sender();

        if (args.length < 2) {
            sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return;
        }

        Channel channel = getChannel(sender, args[0]);
        if (channel == null) {
            return;
        }

        if (doesNotHaveBroadcastPermission(sender, channel)) {
            return;
        }

        String message = convertArgsToString(removeFirstArg(args));
        channel.broadcast(message);

        sendMessage(Message.BROADCAST_SENT, Map.of("{channel}", channel.getName()), sender);
    }

    private boolean doesNotHaveBroadcastPermission(@NotNull CommandSender sender, @NotNull Channel channel) {
        if (Permissions.anyOfOrAdmin(
                sender,
                "strings.*",
                "strings.channels.*",
                "strings.channels." + channel.getName() + ".*",
                "strings.channels." + channel.getName() + ".broadcast"
        )) {
            return false;
        } else {
            sendMessage(Message.NO_PERMS_CHANNEL_BROADCAST, Map.of("{channel}", channel.getName()), sender);
            return true;
        }
    }

    private @NotNull String convertArgsToString(@NotNull String[] args) {
        return String.join(" ", args);
    }

    private @NotNull String[] removeFirstArg(@NotNull String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

}
