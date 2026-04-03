package com.pedestriamc.strings.api.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.event.strings.user.ActiveChannelUpdateEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelJoinEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelLeaveEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelMonitorEvent;
import com.pedestriamc.strings.api.event.strings.user.ChannelUnmonitorEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Interface to create {@link StringsEvent} objects,
 * implemented per platform.
 */
@Internal
public interface EventFactory {

    /**
     * Creates a new {@link StringsChatEvent}
     * @param async Is the event async?
     * @param cancellable Is the event cancellable?
     * @param sender The chat message sender
     * @param message The text content of the message
     * @param recipients Recipients of the message
     * @param channel The Channel the message is being sent to
     * @param signedMessage The SignedMessage if present
     * @return A new StringsChatEvent
     */
    @Contract("_, _, _, _, _, _, _ -> new")
    @NotNull StringsChatEvent chatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            @Nullable SignedMessage signedMessage
    );

    /**
     * Creates a new {@link DirectMessageEvent}
     * @param sender The direct message sender
     * @param recipient The recipient of the direct message
     * @param message The text content of the direct message
     * @return A new DirectMessageEvent
     */
    @Contract("_, _, _ -> new")
    @NotNull DirectMessageEvent directMessage(
            @NotNull StringsUser sender,
            @NotNull StringsUser recipient,
            @NotNull String message
    );

    /**
     * Creates a new {@link MessageDeletionEvent}
     * @param signedMessage The SignedMessage to be deleted
     * @return A new MessageDeletionEvent
     */
    @Contract("_ -> new")
    @NotNull MessageDeletionEvent messageDeletion(@NotNull SignedMessage signedMessage);

    /**
     * Creates a new {@link ChannelJoinEvent}
     * @param user The User
     * @param channel The Channel
     * @return A new Event
     */
    @Contract("_, _ -> new")
    @NotNull ChannelJoinEvent channelJoin(@NotNull StringsUser user, @NotNull Channel channel);

    /**
     * Creates a new {@link ChannelLeaveEvent}
     * @param user The User
     * @param channel The Channel
     * @return A new Event
     */
    @Contract("_, _ -> new")
    @NotNull ChannelLeaveEvent channelLeave(@NotNull StringsUser user, @NotNull Channel channel);

    /**
     * Creates a new {@link ChannelMonitorEvent}
     * @param user The User
     * @param channel The Channel
     * @return A new Event
     */
    @Contract("_, _ -> new")
    @NotNull ChannelMonitorEvent channelMonitor(@NotNull StringsUser user, @NotNull Channel channel);

    /**
     * Creates a new {@link ChannelMonitorEvent}
     * @param user The User
     * @param channel The Channel
     * @return A new Event
     */
    @Contract("_, _ -> new")
    @NotNull ChannelUnmonitorEvent channelUnmonitor(@NotNull StringsUser user, @NotNull Channel channel);


    /**
     * Creates a new {@link ActiveChannelUpdateEvent}
     * @param user The User
     * @param channel The Channel
     * @return A new Event
     */
    @Contract("_, _ -> new")
    @NotNull ActiveChannelUpdateEvent channelActive(@NotNull StringsUser user, @NotNull Channel channel);

}
