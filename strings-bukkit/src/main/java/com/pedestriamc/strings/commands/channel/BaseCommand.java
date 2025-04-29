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
import static com.pedestriamc.strings.api.message.Message.CHANNEL_ACTIVE;

public class BaseCommand extends ChannelProcessor {

    private final Messenger messenger;
    private final UserUtil userUtil;

    public BaseCommand(Strings strings) {
        super(strings);
        messenger = strings.getMessenger();
        userUtil = strings.getUserUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String[] newArgs;

        switch(args.length) {
            case 0 -> {
                messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                return true;
            }
            case 1 -> {
                String channelName = args[0];
                newArgs = new String[2];
                newArgs[0] = " ";
                newArgs[1] = channelName;
            }
            case 2 -> {
                String channelName = args[0];
                String targetName = args[1];
                newArgs = new String[3];
                newArgs[0] = " ";
                newArgs[1] = channelName;
                newArgs[2] = targetName;
            }
            default -> {
                messenger.sendMessage(TOO_MANY_ARGS, sender);
                return true;
            }
        }

        BaseData data = process(sender, newArgs, Type.BASE);

        if(data.shouldReturn()) {
            return true;
        }

        boolean modifyingOther = !sender.equals(data.target());
        User user = userUtil.getUser(data.target());
        Channel channel = data.channel();

        if(!user.memberOf(channel)) {
            user.joinChannel(channel);
            userUtil.saveUser(user);
        }

        if(user.getActiveChannel().equals(channel)) {
            if(modifyingOther) {
                messenger.sendMessage(ALREADY_ACTIVE_OTHER, data.placeholders(), sender);
            } else {
                messenger.sendMessage(ALREADY_ACTIVE, data.placeholders(), sender);
            }
            return true;
        }

        user.setActiveChannel(channel);
        userUtil.saveUser(user);

        if(modifyingOther) {
            messenger.sendMessage(CHANNEL_ACTIVE_OTHER, data.placeholders(), sender);
        }
        messenger.sendMessage(CHANNEL_ACTIVE, data.placeholders(), data.target());

        return true;
    }

}
