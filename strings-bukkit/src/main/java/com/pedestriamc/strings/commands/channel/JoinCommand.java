package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.commands.base.OneToTwoArgAbstractCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

class JoinCommand extends OneToTwoArgAbstractCommand implements CartCommandExecutor {

    JoinCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        int length = data.args().length;
        String[] args = data.args();

        if (checkArgCountAndNotify(sender, length)) {
            return;
        }

        Channel channel = getChannelAndCheckAllows(sender, args[0]);
        if (channel == null) {
            return;
        }

        User target = getTarget(sender, args);
        if (target == null) {
            return;
        }

        if (isAlreadyMember(sender, target, channel)) {
            return;
        }

        target.joinChannel(channel);
        target.setActiveChannel(channel);
        saveUser(target);

        sendFinalMessages(sender, target, channel);
    }

    private boolean isAlreadyMember(@NotNull CommandSender sender, @NotNull StringsUser target, @NotNull Channel channel) {
        return checkConditionAndNotify(
                target.memberOf(channel),
                sender,
                target,
                channel,
                ALREADY_MEMBER,
                ALREADY_MEMBER_OTHER
        );
    }

    private void sendFinalMessages(@NotNull CommandSender sender, @NotNull User target, @NotNull Channel channel) {
        sendAlternateMessages(sender, target, channel, CHANNEL_JOINED, OTHER_USER_JOINED_CHANNEL);
    }

}
