package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

class UnmuteCommand extends AbstractChannelCommand implements CartCommandExecutor {

    UnmuteCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        String[] args = data.args();

        if(isArgCountIncorrect(sender, args.length)) {
            return;
        }

        Channel channel = getChannel(sender, args[0]);
        if(channel == null) {
            return;
        }

        User target = getTarget(sender, args);
        if(target == null) {
            return;
        }

        if(isNotMuted(sender, target, channel)) {
            return;
        }

        target.unmuteChannel(channel);
        saveUser(target);

        sendFinalMessages(sender, target, channel);
    }

    private void sendFinalMessages(@NotNull CommandSender sender, @NotNull User target, @NotNull Channel channel) {
        sendFinalMessages(sender, target, channel, MUTE_SUCCESS, MUTE_SUCCESS_OTHER);
    }

    private boolean isNotMuted(@NotNull CommandSender sender, @NotNull StringsUser target, @NotNull Channel channel) {
        return checkAlreadySet(
                !target.hasChannelMuted(channel),
                sender,
                target,
                channel,
                NOT_MUTED,
                NOT_MUTED_OTHER
        );
    }
}