package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.commands.base.OneToTwoArgAbstractCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

class LeaveCommand extends OneToTwoArgAbstractCommand implements CartCommandExecutor {

    LeaveCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        String[] args = data.args();

        if (checkArgCountAndNotify(sender, args.length)) {
            return;
        }

        Channel channel = getChannel(sender, args[0]);
        if (channel == null) {
            return;
        }

        User target = getTarget(sender, args);
        if (target == null) {
            return;
        }

        if (isNotMember(sender, target, channel)) {
            return;
        }

        target.leaveChannel(channel);
        saveUser(target);

        sendFinalMessages(sender, target, channel);
    }

    private boolean isNotMember(@NotNull CommandSender sender, @NotNull User target, @NotNull Channel channel) {
        return checkConditionAndNotify(
                !target.memberOf(channel),
                sender,
                target,
                channel,
                NOT_CHANNEL_MEMBER,
                NOT_CHANNEL_MEMBER_OTHER
        );
    }

    private void sendFinalMessages(@NotNull CommandSender sender, @NotNull User target, @NotNull Channel channel) {
        sendAlternateMessages(sender, target, channel, LEFT_CHANNEL, LEFT_CHANNEL_OTHER);
    }

}
