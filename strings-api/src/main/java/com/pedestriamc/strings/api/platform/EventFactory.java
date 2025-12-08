package com.pedestriamc.strings.api.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Internal
public interface EventFactory {

    @NotNull
    StringsChatEvent chatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            @Nullable SignedMessage signedMessage
    );


}
