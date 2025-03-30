package com.pedestriamc.strings.commands.channel;


import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;
import static com.pedestriamc.strings.api.message.Message.CHANNEL_JOINED;

/**
 * Channel join sub command
 */
public class JoinCommand extends ChannelProcessor {

    private final Strings strings;
    private final Messenger messenger;
    private final UserUtil userUtil;

    public JoinCommand(Strings strings) {
        super(strings);
        this.strings = strings;
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        BaseData data = process(sender, args, Type.JOIN);

        if(data.shouldReturn()) {
            return true;
        }

        boolean modifyingOther = !sender.equals(data.target());
        User user = strings.getUser(data.target());
        Channel channel = data.channel();

        if(user.memberOf(channel)) {
            if(modifyingOther) {
                messenger.sendMessage(ALREADY_MEMBER_OTHER, data.placeholders(), sender);
            } else {
                messenger.sendMessage(ALREADY_MEMBER, data.placeholders(), sender);
            }
            return true;
        }

        user.joinChannel(channel);
        userUtil.saveUser(user);

        if(modifyingOther) {
            messenger.sendMessage(OTHER_USER_JOINED_CHANNEL, data.placeholders(), sender);
        }
        messenger.sendMessage(CHANNEL_JOINED, data.placeholders(), data.target());

        return true;
    }

}