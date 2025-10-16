package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.MessageContext;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.commands.AbstractCommand;
import com.pedestriamc.strings.impl.MessageableAdapter;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// The command lists all Channels available to the sender, not necessarily all the ones that exist.
@SuppressWarnings("CStyleArrayDeclaration")
class ListCommand extends AbstractCommand implements CartCommandExecutor {

    ListCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        if (data.args().length > 0) {
            messenger().sendMessage(Message.TOO_MANY_ARGS, sender);
        }

        Set<Channel> available = getAllowedChannels(sender);
        MessageContext contexts[] = getContexts(sender, available);

        messenger().batchSend(contexts);
    }

    private @NotNull Set<Channel> getAllowedChannels(@NotNull CommandSender sender) {
        Set<Channel> available = new HashSet<>();
        StringsUser user;
        if (!(sender instanceof Player player)) {
            return strings().getChannelLoader().getChannels();
        } else {
            user = getUser(player);
        }

        for (Channel channel : strings().getChannelLoader().getChannels()) {
            if (channel.allows(user)) {
                available.add(channel);
            }
        }

        return available;
    }

    private MessageContext[] getContexts(@NotNull CommandSender sender, @NotNull Set<Channel> available) {
        if (available.isEmpty()) {
            return new MessageContext[] {
                    MessageContext.of(Message.NO_CHANNELS_AVAILABLE, MessageableAdapter.of(sender), true)
            };
        }

        MessageContext contexts[] = new MessageContext[available.size() + 1];
        contexts[0] = MessageContext.of(Message.CHANNEL_LIST_HEADER, MessageableAdapter.of(sender), false);

        int pos = 1;
        for (Channel channel : available) {
            contexts[pos] = MessageContext.of(
                    Message.CHANNEL_LIST_ENTRY,
                    Map.of("{channel}", channel.getName()),
                    MessageableAdapter.of(sender),
                    false
            );
            pos++;
        }

        return contexts;
    }
}
