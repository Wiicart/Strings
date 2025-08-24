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

    @Contract("_, _ -> new")
    protected @NotNull Container processSymbol(@NotNull String msg, @NotNull User user) {
        for(Map.Entry<String, Channel> entry : channelLoader.getChannelSymbols().entrySet()) {
            if(msg.startsWith(entry.getKey())) {
                Channel c = entry.getValue();
                if (c.allows(user.player()) && !c.equals(user.getActiveChannel())) {
                    msg = msg.substring(entry.getKey().length());
                    return new Container(c, msg);
                }
            }
        }
        return new Container(user.getActiveChannel(), msg);
    }

    protected record Container(Channel channel, String message) {}
}
