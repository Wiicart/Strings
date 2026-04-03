package com.pedestriamc.strings.listener.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.user.User;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Platform.Agnostic
abstract class AbstractChatListener implements Listener {

    private final ChannelManager channelLoader;

    protected AbstractChatListener(@NotNull Strings strings) {
        channelLoader = strings.getChannelLoader();
    }

    /**
     * Routes player messages to the correct channel according to any symbol at the start of a message.<br/>
     * Confirms that the player is eligible to send a message in the said channel.
     * @param msg The player's raw text message.
     * @param user The sender
     * @return A Container
     */
    @Contract("_, _ -> new")
    protected @NotNull AbstractChatListener.MessageRoute processSymbol(@NotNull String msg, @NotNull User user) {
        for (Map.Entry<String, Channel> entry : channelLoader.getChannelSymbols().entrySet()) {
            if (msg.startsWith(entry.getKey())) {
                Channel c = entry.getValue();
                if (c.allows(user) && !c.equals(user.getActiveChannel())) {
                    msg = msg.substring(entry.getKey().length());
                    return new MessageRoute(c, msg);
                }
            }
        }
        return new MessageRoute(user.getActiveChannel(), msg);
    }

    /**
     * A record for returning a Channel and a Message
     * @param channel The determined Channel
     * @param message The message, with symbol removed if applicable.
     */
    protected record MessageRoute(Channel channel, String message) {}
}
