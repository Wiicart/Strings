package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public class LeaveCommand extends ChannelProcessor {

    private final Strings strings;
    private final Messenger messenger;

    public LeaveCommand(Strings strings) {
        super(strings);
        this.strings = strings;
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BaseData data = process(sender, args, Type.LEAVE);
        if(data.shouldReturn()) {
            return true;
        }

        User user = strings.getUser(data.target());
        Channel channel = data.channel();
        boolean modifyingOther = !sender.equals(data.target());

        if(!user.getChannels().contains(channel)) {
            if(modifyingOther) {
                messenger.sendMessage(NOT_CHANNEL_MEMBER_OTHER, data.placeholders(), sender);
            }else{
                messenger.sendMessage(NOT_CHANNEL_MEMBER, data.placeholders(), data.target());
            }
            return true;
        }

        if(channel.getName().equals("default")) {
            messenger.sendMessage(CANT_LEAVE_GLOBAL, sender);
            return true;
        }

        user.leaveChannel(channel);
        if(modifyingOther) {
            messenger.sendMessage(OTHER_USER_LEFT_CHANNEL, data.placeholders(), sender);
        }
        messenger.sendMessage(LEFT_CHANNEL, data.placeholders(), data.target());

        return true;
    }
}
