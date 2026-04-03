package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered after a player has joined and the {@link StringsUser} of the player has been loaded.
 */
public class StringsUserLoadEvent implements StringsEvent {

    private final StringsUser user;

    public StringsUserLoadEvent(@NotNull StringsUser user) {
        this.user = user;
    }

    @NotNull
    public StringsUser getUser() {
        return user;
    }

}
