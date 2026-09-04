package com.pedestriamc.strings.common.chat.messages.dispatch;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

abstract class AbstractMessageDispatcher implements MessageDispatcher {

    private final StringsPlatform strings;
    private final Channel channel;

    AbstractMessageDispatcher(@NotNull StringsPlatform strings, @NotNull Channel channel) {
        this.strings = strings;
        this.channel = channel;
    }

    @Override
    public void dispatch(@NotNull StringsUser user, @NotNull String message) {
        Set<StringsUser> recipients = channel.getRecipients(user);

        if (channel.callsEvents()) {
            dispatchEventMessage(user, message, recipients);
        } else {
            dispatchMessageToPlayers(user, message, recipients);
        }
    }

    protected void dispatchEventMessage(@NotNull StringsUser user, @NotNull String message, @NotNull Set<StringsUser> recipients) {
        ChannelChatEvent event = strings.eventFactory().chatEvent(
                false,
                true,
                user,
                ComponentConverter.toComponent(message),
                recipients,
                channel,
                null
        );

        strings.eventManager().dispatch(event);
        if (!event.isCancelled()) {
            dispatchMessageToPlayers(user, ComponentConverter.toString(event.message()), recipients);
        }
    }

    /**
     * Dispatches a message to all recipients.
     * No modifications to recipients or the message should occur from this point,
     * formatting should be based on Channel formatting.
     * @param user The sender
     * @param message The raw message from the sender.
     * @param recipients The recipients of the message.
     */
    abstract void dispatchMessageToPlayers(@NotNull StringsUser user, @NotNull String message, @NotNull Set<StringsUser> recipients);

    protected StringsPlatform strings() {
        return strings;
    }

    protected Channel channel() {
        return channel;
    }


}
