package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.ChannelChatEvent2;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BukkitEventFactory implements EventFactory {

    public BukkitEventFactory() {
        // no need to do anything here
    }

    @Override
    public @NotNull StringsChatEvent chatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            @Nullable SignedMessage signedMessage
    ) {
        return new ChannelChatEvent2(async, cancellable, sender, message, recipients, channel, signedMessage);
    }
}
