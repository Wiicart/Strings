package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public interface StringsChatEvent extends StringsEvent {

    /**
     * Provides the Channel the message was sent in.
     * @return The Channel
     */
    @NotNull
    Channel getChannel();

    /**
     * Provides a Set of the players receiving the message
     * @return A modifiable Set
     */
    @NotNull
    Set<StringsUser> getMessageRecipients();

    /**
     * Provides the sender of the message.
     * @return The sender
     */
    @NotNull
    StringsUser getSender();

    /**
     * Provides the text of the message being set.
     * This may include color codes.
     * @return A String
     */
    @NotNull
    String getMessage();

    /**
     * Allows updating the message.
     * @param message The new message
     */
    void setMessage(@NotNull String message);

    /**
     * Provides the SignedMessage correlated with the message, if one exists.
     * @return An Optional
     */
    @NotNull
    Optional<SignedMessage> getSignedMessage();

    /**
     * Sets if this Event is canceled or not.
     * @param cancel If the event should be canceled or not
     */
    void setCancelled(boolean cancel);

    /**
     * Tells if this event has been canceled
     * @return true/false
     */
    boolean isCancelled();

}
