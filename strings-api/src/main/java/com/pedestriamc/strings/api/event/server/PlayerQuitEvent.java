package com.pedestriamc.strings.api.event.server;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@ApiStatus.Internal
public class PlayerQuitEvent implements ServerEvent {

    private final StringsUser user;

    @ApiStatus.Internal
    public PlayerQuitEvent(@NotNull StringsUser user) {
        this.user = user;
    }

    /**
     * Provides the UUID of the Player
     * @return The UUID
     */
    @NotNull
    public UUID getPlayerUniqueId() {
        return user.getUniqueId();
    }

    /**
     * Provides the StringsUser of the Player
     * @return The StringsUser
     */
    @NotNull
    public StringsUser getPlayer() {
        return user;
    }

}
