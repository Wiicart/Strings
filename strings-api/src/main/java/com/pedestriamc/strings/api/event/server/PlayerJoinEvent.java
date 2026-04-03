package com.pedestriamc.strings.api.event.server;

import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fired when a Player joins the server.
 */
public interface PlayerJoinEvent extends ServerEvent {

    /**
     * Provides the UUID of the Player
     * @return The UUID
     */
    @NotNull UUID getPlayerUniqueId();

    /**
     * Provides the StringsUser of the Player
     * @return The StringsUser
     */
    @NotNull StringsUser getPlayer();

    /**
     * Provides the join message.
     * @return A Component.
     */
    @NotNull Component getMessage();

    /**
     * Sets the join message
     * @param message A non-null Component.
     */
    void setMessage(@NotNull Component message);

}
