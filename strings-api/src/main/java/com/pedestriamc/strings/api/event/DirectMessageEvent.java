package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.event.strings.Cancellable;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

/**
 * Fires every time a user sends a message directly to a player using <code>/msg (player) (message)</code>
 */
public interface DirectMessageEvent extends StringsEvent, Cancellable {

    /**
     * Provides the raw message that the sender sent.
     * @return A String
     */
    @NotNull
    String getMessage();

    /**
     * Sets the message that the sender sends to the recipient
     * @param message The new message
     */
    void setMessage(@NotNull String message);

    /**
     * Provides the sender of the direct message
     * @return A StringsUser
     */
    @NotNull
    StringsUser getSender();

    /**
     * Provides the recipient of the direct message.
     * @return A StringsUser
     */
    @NotNull
    StringsUser getRecipient();

}
