package com.pedestriamc.strings.common.chat.messages.dispatch;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public interface MessageDispatcher {

    /**
     * Dispatches a message to users.
     * @param user The message sender.
     * @param message The raw message content.
     */
    void dispatch(@NotNull StringsUser user, @NotNull String message);

}
